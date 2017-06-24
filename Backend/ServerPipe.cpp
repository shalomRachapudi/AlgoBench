/*
* File:   ServerName.cpp
* Author: eziama
*
* Created on July 06, 2015, 15:29 PM
*/
#include "stdafx.h"
#include "ServerPipe.h"


inf2b::ServerPipe::ServerPipe() {
    m_isConnected = false;
#ifdef PREDEF_OS_WINDOWS
    m_pipeHandle = INVALID_HANDLE_VALUE, hThread = NULL;
    m_pipeName = TEXT( "\\\\.\\pipe\\mynamedpipe" );
#endif
}

inf2b::ServerPipe::~ServerPipe() { }


int inf2b::ServerPipe::run() {

    // The main loop creates an instance of the named pipe and 
    // then waits for a client to connect to it. When the client 
    // connects, a thread is created to handle communications 
    // with that client, and this loop is free to wait for the
    // next client connect request. It is an infinite loop.

#ifdef PREDEF_OS_WINDOWS
    _tprintf( TEXT( "\nPIPE SERVER: Waiting for client connection on %s\n" ), m_pipeName );
    m_pipeHandle = CreateNamedPipe(
            m_pipeName,               // pipe name 
            PIPE_ACCESS_DUPLEX,       // read/write access 
            PIPE_TYPE_MESSAGE | PIPE_READMODE_MESSAGE | PIPE_WAIT, // message type pipe | message-read mode | blocking mode
            PIPE_UNLIMITED_INSTANCES, // max. instances  
            DEFAULT_BUFFERSIZE,       // output buffer size 
            DEFAULT_BUFFERSIZE,       // input buffer size 
            0,                        // client time-out 
            NULL                      // default security attribute 
        );

    if ( m_pipeHandle == INVALID_HANDLE_VALUE ) {
        _tprintf( TEXT( "CreateNamedPipe failed, GLE=%d.\n" ), GetLastError() );
        return -1;
    }

    ULONG serverPid = 0L;
    std::cout << serverPid << std::endl;
    if ( GetNamedPipeServerProcessId( m_pipeHandle, &serverPid ) ) {
        std::cout << serverPid << std::endl;
    }
    else {
        std::cout << "ERROR; Couldn't retrieve server PID. Code: " << GetLastError() << std::endl;
    }
    // Wait for the client to connect; if it succeeds, 
    // the function returns a nonzero value. If the function
    // returns zero, GetLastError returns ERROR_PIPE_CONNECTED. 

    m_isConnected = ConnectNamedPipe( m_pipeHandle, NULL ) ? TRUE : ( GetLastError() == ERROR_PIPE_CONNECTED );
    
    if ( m_isConnected ) {
        printf( "Client connected!\n" );

        // do the rest of the sending and receiving here
        communicate();
    }
    else
        // The client could not connect, so close the pipe. 
        CloseHandle( m_pipeHandle );

#endif
    return 0;
}

int inf2b::ServerPipe::communicate() 
{
#ifdef PREDEF_OS_WINDOWS
    HANDLE hHeap = GetProcessHeap();
    TCHAR* pchReply = ( TCHAR* ) HeapAlloc( hHeap, 0, DEFAULT_BUFFERSIZE * sizeof( TCHAR ) );

    DWORD bytesRead = 0, messageSize = 0, written = 0;
    BOOL fSuccess = FALSE;

    // Do some extra error checking since the app will keep running even if this
    // thread fails.
    if ( m_pipeHandle == NULL ) {
        printf( "\nERROR - Pipe Server Failure:\n" );
        printf( "   InstanceThread got an unexpected NULL value in lpvParam.\n" );
        printf( "   InstanceThread exitting.\n" );
        if ( pchReply != NULL ) HeapFree( hHeap, 0, pchReply );
        return ( DWORD ) -1;
    }

    if ( pchReply == NULL ) {
        printf( "\nERROR - Pipe Server Failure:\n" );
        printf( "   InstanceThread got an unexpected NULL heap allocation.\n" );
        printf( "   InstanceThread exitting.\n" );
        return ( DWORD ) -1;
    }

    // write to client first
    std::string serverMessage = "Message from SERVER: Run the following instructions: \nQUICKSORT\n10000-10000000\nLEFT-PIVOT\nRANDOM, SORTED, WORST-CASE\nQUICKSORT\n10000-10000000\nLEFT-PIVOT\nRANDOM, SORTED, WORST-CASE\nQUICKSORT\n10000-10000000\nLEFT-PIVOT\nRANDOM, SORTED, WORST-CASE\nQUICKSORT\n10000-10000000\nLEFT-PIVOT\nRANDOM, SORTED, WORST-CASE\nQUICKSORT\n10000-10000000\nLEFT-PIVOT\nRANDOM, SORTED, WORST-CASE\nQUICKSORT\n10000-10000000\nLEFT-PIVOT\nRANDOM, SORTED, WORST-CASE\nQUICKSORT\n10000-10000000\nLEFT-PIVOT\nRANDOM, SORTED, WORST-CASE\nQUICKSORT\n10000-10000000\nLEFT-PIVOT\nRANDOM, SORTED, WORST-CASE\nQUICKSORT\n10000-10000000\nLEFT-PIVOT\nRANDOM, SORTED, WORST-CASE\nQUICKSORT\n10000-10000000\nLEFT-PIVOT\nRANDOM, SORTED, WORST-CASE\n";
    messageSize = ( serverMessage.length() + 1 ) * sizeof( char ); // + 1 writes the null-terminator
    fSuccess = WriteFile(
            m_pipeHandle,        // handle to pipe 
            serverMessage.c_str(),     // buffer to write from 
            messageSize, // number of bytes to write 
            &written,   // number of bytes written 
            NULL     // not overlapped I/O 
        );       
    _tprintf( TEXT( "Message size: %d bytes\n" ), messageSize );
    _tprintf( TEXT( "Wrote %d bytes\n" ), written );

    if ( !fSuccess || messageSize != written ) {
        _tprintf( TEXT( "InstanceThread WriteFile failed, GLE=%d.\n" ), GetLastError() );
    }

    char receiveBuffer[ DEFAULT_BUFFERSIZE ];
    // Loop until done reading
    do {
        // Read client requests from the pipe. This simplistic code only allows messages
        // up to DEFAULT_BUFFERSIZE characters in length.
        fSuccess = ReadFile(
                m_pipeHandle,             // handle to pipe 
                &receiveBuffer,             // buffer to receive data 
                DEFAULT_BUFFERSIZE - 1,  // size of buffer (reserve space for '\0')
                &bytesRead,             // number of bytes read 
                NULL                  // not overlapped I/O 
            );

        if ( !fSuccess && GetLastError() != ERROR_MORE_DATA ) {
            break;
        }
        receiveBuffer[ bytesRead ] = '\0';
        std::cout << receiveBuffer << std::flush;
    } while ( 1 );

    if ( !fSuccess || bytesRead == 0 ) {
        if ( GetLastError() == ERROR_BROKEN_PIPE ) {
            _tprintf( TEXT( "SERVER: Client disconnected.\n" ), GetLastError() );
        }
        else {
            _tprintf( TEXT( "SERVER: ReadFile failed, GLE=%d.\n" ), GetLastError() );
        }
    }
    // Flush the pipe to allow the client to read the pipe's contents 
    // before disconnecting. Then disconnect the pipe, and close the 
    // handle to this pipe instance. 

    FlushFileBuffers( m_pipeHandle );
    DisconnectNamedPipe( m_pipeHandle );
    CloseHandle( m_pipeHandle );

    HeapFree( hHeap, 0, pchReply );

#endif
    return 0;
}
