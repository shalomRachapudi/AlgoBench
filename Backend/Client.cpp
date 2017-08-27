/*
  * The MIT License
  *
  * Copyright 2015 Eziama Ubachukwu (eziama.ubachukwu@gmail.com).
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in
  * all copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  * THE SOFTWARE.
  */

/*
* File:   Client.cpp
* Author: eziama
*
* Created on July 2, 2015, 3:30 AM
*/

#include "stdafx.h"
#include "Client.h"
using namespace inf2b;


void Client::run( int p ) 
{
    m_port = p;
    int result;
    // Windows portion
#if defined PREDEF_OS_WINDOWS
    DLLVERSION = MAKEWORD( 2, 1 );
    // start WinSock DLL
    result = WSAStartup( DLLVERSION, &WinSockData );
    if ( result != NO_ERROR ) {
        std::cerr << "CLIENT: Couldn't start WinSock DLL (WSAStartup())" << std::endl;
        return;
    }
    // point the function pointer to the appropriate Win method
    m_getError = [] () {
        return WSAGetLastError();
    };
#elif defined PREDEF_OS_LINUX || defined PREDEF_OS_MACOSX
    // TODO: This might not work!! Capturing state is necessary
    m_getError = [] () {
        return errno;
    };
    memset( &m_address, 0, sizeof( m_address ) );
#endif
    // Create sockets for connecting to SERVER
    m_connectionSocket = socket( AF_INET, SOCK_STREAM, IPPROTO_TCP );
#if defined PREDEF_OS_WINDOWS
    m_address.sin_addr.s_addr = inet_addr( LOCAL_ADDRESS );// set ip for windows
#elif defined PREDEF_OS_LINUX || defined PREDEF_OS_MACOSX
    inet_pton( AF_INET, LOCAL_ADDRESS, &( m_address.sin_addr ) ); // set ip for linux
#endif
    m_address.sin_family = AF_INET;
    m_address.sin_port = htons( m_port ); // port
    
    // try to connect 5 times
    int maxConnectionAttempts = 5;
    do {
        if ( connect( m_connectionSocket, ( SocketAddress* )& m_address, sizeof( m_address ) ) != SocketError ) {
            std::cout << "\nConnected to SERVER!" << std::endl;
            break;
        }
        else if ( maxConnectionAttempts > 0 ) {
            // if server isn't waiting yet, retry in a sec
            if ( m_getError() == ConnectionRefusedError ) {
                std::cout << "[TASKRUNNER] Connection to server refused. Retrying..." << std::endl;
                std::this_thread::sleep_for( std::chrono::milliseconds( 500 ) );
            }
            else {
                std::cout << "[TASKRUNNER] Connection to server failed. Retrying..." << std::endl;
                std::this_thread::sleep_for( std::chrono::milliseconds( 1000 ) );
            }
        }
        else {
            std::cerr << "Failed to connect. Make sure the server is running, then relaunch.\n";
            perror( "Error" );
            std::cout << "[TASKRUNNER] Terminating CLIENT..." << std::endl;
            exit( 1 );
        }
    } while ( --maxConnectionAttempts >= 0 );
    
    // receive from SERVER
    receiveCommand();

    // indicate that instructions have been received from server and this is ready to send
    socketReadySemaphore.notify();
    m_ready = 1;

    // send results to server
    sendResults();

    // close the socket
    result = closeSocket();
    if ( result == SocketError ) {
        std::cerr << "Close failed with error: " << m_getError() << std::endl;
        return;        
    }
}

void Client::receiveCommand() 
{
    int receiveBufferLen = DEFAULT_BUFFERSIZE;
    char receiveBuffer[DEFAULT_BUFFERSIZE];
    int result;
    
    // set receive timeout
    //timeval tv; // the time value struct
    //tv.tv_sec = 3000;
    //tv.tv_usec = 0;
    //setsockopt(m_connectionSocket, SOL_SOCKET, SO_RCVTIMEO, (char *)&tv, sizeof(tv));
    
    std::vector< char > instruction;
    do {
        result = recv( m_connectionSocket, receiveBuffer, receiveBufferLen - 1, 0 );
        if ( result > 0 ) {
            receiveBuffer[ result ] = '\0';
            m_instructionString += receiveBuffer;
        }
        else if ( result == 0 ) {
            std::cout << "CLIENT: Nothing more to receive.\n" << std::endl;
        }
        else {
            std::cout << "Error: " << m_getError() << std::endl;
        }
    } while ( result > 0 );
    
    if ( result > 0 ) {
        exit( 1 );
    }
}

void Client::sendResults() 
{
    // send to SERVER
    long sentBytes;
    std::string sendData = "BEGIN\n";
    std::cout << "CLIENT: sending to SERVER..." << std::endl;
    // set send timeout
    //timeval tv2; // the time value struct - already set for receive above
    //tv2.tv_sec = 30;
    //tv2.tv_usec = 0;
    //setsockopt(m_connectionSocket, SOL_SOCKET, SO_RCVTIMEO, (char *)&tv2, sizeof(tv2));
    do {
        // be nice - allow others
        std::this_thread::yield();
        // wait till there's something to send
        // lock buffer to avoid the producer coming back to change it within this period.
        // wait() acquires the lock before proceeding. This same lock is reused by the producer
        // wait() releases the lock while waiting, until it gets notify-ed.
        if ( progressSemaphore.waitFor( std::chrono::milliseconds( 2000 ) ) ) {
            for ( auto s : progressReport ) {
                // wouldn't loop if nothing is in the vector<>, but notify() could have been called
                // by calling reportProgress("")
                sentBytes = send( m_connectionSocket, s.c_str(), ( int ) s.length(), 0 );
                if ( sentBytes == -1 ) {
                    break;
                }
            }
            // reset buffer
            progressReport.clear();
        }
        
        // send heartbeat message anyway
        std::this_thread::yield();
        sentBytes = send( m_connectionSocket, HEARTBEAT_CHAR, 2, 0 );
    } while ( m_ready == 1 && sentBytes != -1 ); // -1 when connection closes
    
    // raise an interrupt signal when this happens, since normally the connection should stay open
    // and client should end before server
    if ( sentBytes == -1 ) {
        std::cout << "Server connection has been closed! Error Code: " << m_getError() << std::endl;
        std::cout << "[TASKRUNNER] Exiting ..." << std::endl;
        exit( 1 );
    }
    
    sendData = "END\n";
    sentBytes = send( m_connectionSocket, sendData.c_str(), ( int ) sendData.length(), 0 );    
}


void Client::signalEnd() 
{
    m_ready = 0;    
}

std::string& Client::getInstructionString() 
{
    return m_instructionString;    
}

int Client::closeSocket() 
{
    int r = -1;
#if defined PREDEF_OS_WINDOWS
    r = closesocket( m_connectionSocket );
    WSACleanup();
    return r;
#elif defined PREDEF_OS_LINUX || defined PREDEF_OS_MACOSX
    r = close( m_connectionSocket );
#endif
    return r;    
}
