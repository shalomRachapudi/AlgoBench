
/*
* File:   Server.cpp
* Author: eziama
*
* Created on July 1, 2015, 11:29 PM
*/

#include "stdafx.h"
#include "Server.h"
using namespace inf2b;

Server::Server() 
{}

void Server::run( int p ) {
    // Windows portion
    int result;
    m_port = p;
#if defined PREDEF_OS_WINDOWS
    DLLVERSION = MAKEWORD( 2, 1 );
    // start WinSock DLL
    result = WSAStartup( DLLVERSION, &WinSockData );
    if ( result != NO_ERROR ) {
        std::cerr << "Couldn't start WinSock DLL (WSAStartup())" << std::endl;
        return;
    }
    // call the approp function
    getError = [] () {
        return WSAGetLastError();
    };
    // create socket structure
    int addressSize = sizeof( m_address );

#elif defined PREDEF_OS_LINUX || defined PREDEF_OS_MACOSX
    getError = [] () {
        return errno;
    };
    memset( &m_address, 0, sizeof( m_address ) );
    // create socket structure
    socklen_t addressSize = sizeof( m_address );

#endif

    // Create sockets:
    // socket for maintaining connections
    m_connectionSocket = socket( AF_INET, SOCK_STREAM, IPPROTO_TCP );
    // socket for listening for connections
    inet_pton( AF_INET, LOCAL_ADDRESS, &( m_address.sin_addr ) ); // set ip
    m_address.sin_family = AF_INET;
    m_address.sin_port = htons( m_port ); // port
    m_address.sin_addr.s_addr = INADDR_ANY;

    m_listenSocket = socket( AF_INET, SOCK_STREAM, IPPROTO_TCP );
    result = bind( m_listenSocket, ( SocketAddress* )& m_address, addressSize );
    if ( result == SocketError ) {
        std::cerr << "Failed to bind to listening socket." << std::endl;
        closeSocket();
        return;
    }
    listen( m_listenSocket, 10 ); // use SOMAXCONN for unlimited connections (0x7fffffff)

    if ( getsockname( m_listenSocket, ( SocketAddress* )& m_address, &addressSize ) == -1 )
        std::cerr << "Error in getting port number: getsockname() error." << std::endl;
    else
        std::cout << "Connected to port " << ntohs(m_address.sin_port) << std::endl;

    std::cout << "SERVER: Waiting for connection..." << std::endl;
    if ( ( m_connectionSocket = accept( m_listenSocket, ( SocketAddress* )& m_address, &addressSize ) ) == SocketError ) {
        std::cerr << "Failed to accept. Error: " << getError() << std::endl;
        closeSocket();
        //closeSocket();
        return;
    }
    std::cout << "SERVER: Connected to client!" << std::endl;
    // send data to client
    long sentBytes;
    std::string sendData[] = { "ALGORITHM:10\nALGORITHM-GROUP:1\nINPUT-STARTSIZE:10000\nINPUT-STEPSIZE:10000\nINPUT-FILENAME:C:\\Users\\eziama\\Desktop\\iniput.txt\nNUMRUNS:50\nNUMREPEATS:1\nINPUT-MINVALUE:0\nINPUT-MAXVALUE:100000000\nINPUT-DISTRIBUTION:1\nQUICKSORT-PIVOT-POSITION:1",
        "ALGORITHM:20\nALGORITHM-GROUP:2\nINPUT-STARTSIZE:10000\nINPUT-STEPSIZE:10000\nINPUT-FILENAME:\nNUMRUNS:50\nNUMREPEATS:1\nINPUT-MINVALUE:0\nINPUT-MAXVALUE:100000000\nINPUT-DISTRIBUTION:1",
        "ALGORITHM:30\nALGORITHM-GROUP:3\nINPUT-STARTSIZE:0\nINPUT-STEPSIZE:1000000\nINPUT-FILENAME:\nNUMRUNS:100\nNUMREPEATS:1\nGRAPH-STRUCTURE:1\nGRAPH-FIXED-SIZE:1000000\nGRAPH-FIXED-EDGES:0\nGRAPH-ALLOW-SELF-LOOP:0\nGRAPH-IS-DIRECTED:0\n",
        "ALGORITHM:40\nALGORITHM-GROUP:4\nINPUT-STARTSIZE:500000\nINPUT-STEPSIZE:1\nINPUT-FILENAME:\nNUMRUNS:1\nNUMREPEATS:1\nHASH-BUCKET-ARRAY-SIZE:100\nHASH-FUNCTION-TYPE:2\nHASH-KEY-TYPE:1"};
    // the algo index to run
    int algo = 3;
    sentBytes = send( m_connectionSocket, sendData[ algo ].c_str(), ( int ) sendData[ algo ].size(), 0 );
    std::cout << sentBytes << " bytes sent to CLIENT out of " << ( int ) sendData[ algo ].size()
        << " bytes." << std::endl;
    // shutdown connectionSocket since no more data will be sent
    result = shutdown( m_connectionSocket, SocketSendPart );
    // receive from client
    std::cout << "Now waiting to receive from CLIENT" << std::endl;
    char receiveBuffer[ DEFAULT_BUFFERSIZE ];
    // set timeout for sending
    //timeval tv2;
    //tv2.tv_sec = 10000000L;
    //setsockopt(connectionSocket, SOL_SOCKET, SO_RCVTIMEO, (char *) &tv2, sizeof(tv2));
    do {
        result = recv( m_connectionSocket, &receiveBuffer[ 0 ], DEFAULT_BUFFERSIZE - 1, 0 );
        if ( result > 0 ) {
            // check if heartbeat message
            if ( result == 2 && receiveBuffer[ 0 ] == HEARTBEAT_CHAR[ 0 ] ) {
                std::cout << "Heartbeat" << std::endl;
                continue;
            }
            receiveBuffer[ result ] = '\0';
            std::cout << receiveBuffer << std::flush;
        }
        else if ( result == 0 ) {
            std::cout << "Nothing more to receive from CLIENT." << std::endl;
        }
        else {
            std::cout << "Connection closed prematurely!" << std::endl;
            std::cout << "Error: " << getError() << std::endl;
        }
    } while ( result > 0 );

    //std::this_thread::sleep_for(std::chrono::milliseconds(5000));

    // close the socket
    result = closeSocket();
    if ( result == SocketError ) {
        std::cerr << "Close failed with error: " << getError() << std::endl;
        return;
    }
}

int Server::closeSocket() {
    int r = -1;
#if defined PREDEF_OS_WINDOWS
    closesocket( m_connectionSocket );
    r = closesocket( m_listenSocket );
    WSACleanup();
#elif defined PREDEF_OS_LINUX || defined PREDEF_OS_MACOSX
    close( m_connectionSocket );
    r = close( m_listenSocket );
#endif
    return r;
}

Server::Server( const Server& orig ) 
{}

Server::~Server()
{}

