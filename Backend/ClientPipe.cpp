/*
* File:   ClientPipe.cpp
* Author: eziama
*
* Created on July 06, 2015, 15:29 PM
*/
#include "stdafx.h"
#include "ClientPipe.h"
#pragma comment(lib, "kernel32.lib")

using namespace inf2b;

ClientPipe::ClientPipe() 
{
#if defined PREDEF_OS_WINDOWS
    m_pipeName = TEXT("\\\\.\\pipe\\mynamedpipe");
    m_ready = 0;
#endif
}

void ClientPipe::run() 
{
#if defined PREDEF_OS_WINDOWS
    BOOL   fSuccess = FALSE;
    DWORD  dwMode;

    // Try to open a named pipe; wait for it, if necessary. 
    m_pipeHandle = CreateFile(
            m_pipeName,   // pipe name 
            GENERIC_READ | GENERIC_WRITE,   // read and write access 
            0,              // no sharing 
            NULL,           // default security attributes
            OPEN_EXISTING,  // opens existing pipe 
            0,              // default attributes 
            NULL            // no template file 
        );          

    ULONG clientPid = 0L;
    std::cout << clientPid << std::endl;
    
    if ( GetNamedPipeClientProcessId( m_pipeHandle, &clientPid ) ) {
        std::cout << clientPid << std::endl;
    }
    else {
        std::cout << "ERROR: Couldn't retrieve client PID. Code: " << GetLastError() << std::endl;
    }
    
    // Break if the pipe handle is valid. 
    if ( m_pipeHandle == INVALID_HANDLE_VALUE ) {
        _tprintf( TEXT( "Pipe handle is invalid. GLE=%d\n" ), GetLastError() );
        // Exit if an error other than ERROR_PIPE_BUSY occurs.
        if ( GetLastError() != ERROR_PIPE_BUSY ) {
            _tprintf( TEXT( "Could not open pipe. GLE=%d\n" ), GetLastError() );
            return;
        }
        // All pipe instances are busy, so wait for 20 seconds. 
        printf( "All pipe instances are busy, so wait for 20 seconds." );

        if ( !WaitNamedPipe( m_pipeName, 20000 ) ) {
            printf( "Could not open pipe: 20 second wait timed out." );
            return;
        }
    }
    // The pipe connected; change to message-read mode. 
    dwMode = PIPE_READMODE_MESSAGE;
    fSuccess = SetNamedPipeHandleState(
            m_pipeHandle,    // pipe handle 
            &dwMode,  // new pipe mode 
            NULL,     // don't set maximum bytes 
            NULL // don't set maximum time 
        );    

    if ( !fSuccess ) {
        _tprintf( TEXT( "SetNamedPipeHandleState failed. GLE=%d\n" ), GetLastError() );
        return;
    }

    std::cout << "Connected to SERVER!" << std::endl;

    // Receive from the pipe. 
    DWORD  bytesReceived, bytesWritten;
    char  receiveBuffer[ DEFAULT_BUFFERSIZE ];
    std::cout << "Receiving from SERVER..." << std::endl;
    
    do {
        fSuccess = ReadFile(
                m_pipeHandle,    // pipe handle 
                receiveBuffer,    // buffer to receive reply 
                ( DEFAULT_BUFFERSIZE - 1 ) * sizeof( char ),  // size of buffer: - 1 to accommodate \0 
                &bytesReceived,  // number of bytes read 
                NULL             // not overlapped 
            ); 

        if ( !fSuccess && GetLastError() != ERROR_MORE_DATA ) { break; }
        receiveBuffer[ ( bytesReceived ) / sizeof( char ) ] = '\0';
        std::cout << receiveBuffer << std::flush;
        //_tprintf(TEXT("\nBytes received: %d\n"), bytesReceived);
    } while ( !fSuccess );  // repeat loop if ERROR_MORE_DATA 

    if ( !fSuccess ) { // end the backend client
        _tprintf( TEXT( "ReadFile from pipe failed. GLE=%d\n" ), GetLastError() );
        CloseHandle( m_pipeHandle );
        // exit
        raise( SIGINT );
    }
    std::cout << "Client ready to run!" << std::endl;
    m_ready = 1;

    // monitor state of socket in a different thread, in case we're sem.wait()-ing on a long
    // job before we can check to see that server has closed. This would make the client end
    // close immediately the main app does, rather than waiting to find out only when we call
    // send(...) AFTER new data is written to the output buffer. New write might take forever!
    std::thread socketMonitorThread = std::thread( [&] {
        bool isAlive;
        std::string heartBeatMsg = "";
        DWORD byteWritten;
        while ( 1 ) {
            std::this_thread::sleep_for( std::chrono::milliseconds( 2000 ) );
            isAlive = WriteFile(
                    m_pipeHandle,     // pipe handle 
                    heartBeatMsg.c_str(),      // message as void*
                    heartBeatMsg.length(),     // message length as int
                    &byteWritten,  // bytes written as int*
                    0              // not overlapped 
                ); 
            if ( isAlive ) {
                std::cout << "Pipe still open" << GetLastError();
            }
            else {
                std::cout << "Pipe state error: code " << GetLastError();
                raise( SIGINT );
            }
            std::cout << std::endl;
        }
    } );

    // Send a message to the pipe server.
    do {
        // delayed exit
        std::this_thread::sleep_for( std::chrono::milliseconds( 5000 ) );
        // wait till there's something to send
        // lock buffer to avoid the producer coming back to change it within this period.
        // wait() acquires the lock before proceeding. This same lock is reused by the producer
        // wait() releases the lock while waiting, until it gets notify-ed.
        progressSemaphore.wait();
        for ( auto s : progressReport ) {
            // wouldn't loop if nothing is in the vector<>, but notify() could have been called
            // by calling reportProgress("")
            fSuccess = WriteFile(
                    m_pipeHandle,   // pipe handle 
                    s.c_str(),      // message as void*
                    s.length(),     // message length as int
                    &bytesWritten,  // bytes written as int*
                    NULL            // not overlapped 
                );
            std::cout << "Maybe sent" << std::endl;
        }
        std::cout << "Tried sending" << std::endl;
        // reset buffer
        progressReport.clear();
    } while ( m_ready == 1 && fSuccess );

    // raise an interrupt signal if write fails, since normally the connection should stay open
    // and client should end before server

    if ( !fSuccess ) {
        _tprintf( TEXT( "WriteFile to pipe failed. GLE=%d\n"), GetLastError() );
        CloseHandle( m_pipeHandle );
        raise( SIGINT );
    }
#endif
    return;
}

int ClientPipe::connectionReady() 
{ return m_ready; }

void ClientPipe::signalEnd() 
{ m_ready = 0; }

ClientPipe::~ClientPipe() 
{
#if defined PREDEF_OS_WINDOWS
    CloseHandle( m_pipeHandle );
#endif
}
