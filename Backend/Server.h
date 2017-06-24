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
* File:   Server.h
* Adapted from Charles Germany's tutorial: https://www.youtube.com/watch?v=WaLQ7s1X6wo
* Author: eziama ubachukwu 
*
* Created on July 1, 2015, 11:29 PM
*/

#ifndef SERVER_H
#define SERVER_H

#pragma once
#if defined PREDEF_OS_WINDOWS
// Add "Ws2_32.lib" to the LINKER settings:
#pragma comment(lib, "Ws2_32.lib")

#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif
#define SocketAddressIn SOCKADDR_IN
#define SocketAddress SOCKADDR
#define SCK_VERSION2 0X0202 // Winsock version 2
// errors
#define SocketError SOCKET_ERROR
#define ConnectionRefusedError WSAECONNREFUSED
#define SocketSendPart SD_SEND
#elif defined PREDEF_OS_LINUX || defined PREDEF_OS_MACOSX
#define SocketAddressIn sockaddr_in
#define SocketAddress sockaddr
#define SocketError -1
#define ConnectionRefusedError ECONNREFUSED
#define SocketSendPart SHUT_WR
#endif

#define LOCAL_ADDRESS "127.0.0.1"
#define DEFAULT_BUFLEN 512

#include "stdafx.h"
#include "global.h"


namespace inf2b
{
    class Server
    {
    private:
#if defined PREDEF_OS_WINDOWS
        WSAData WinSockData;
        WORD DLLVERSION;
        SOCKADDR_IN m_address; // socket structure
        // sockets
        SOCKET m_listenSocket;
        SOCKET m_connectionSocket;
#elif defined PREDEF_OS_LINUX || defined PREDEF_OS_MACOSX
        struct sockaddr_in m_address; // socket structure
        int m_connectionSocket;
        int m_listenSocket;
#endif
        int closeSocket();
        int( *getError )();
        int m_port;

    public:
        Server();
        Server( const Server& orig );
        virtual ~Server();
        void run( int );
    };
}

#endif
