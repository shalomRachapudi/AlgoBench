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
* File:   Client.h
* Author: eziama
*
* Created on July 2, 2015, 3:30 AM
*/

#ifndef CLIENT_H
#define CLIENT_H

#pragma once

#include "stdafx.h"
#include "global.h"
#include "Server.h"

namespace inf2b
{

    class Client 
    {
    private:
#if defined PREDEF_OS_WINDOWS
        WSAData WinSockData;
        WORD DLLVERSION;
        SOCKADDR_IN m_address; // socket structure
        SOCKET m_connectionSocket;

#elif defined PREDEF_OS_LINUX || defined PREDEF_OS_MACOSX
        struct sockaddr_in m_address; // socket structure
        int m_connectionSocket;

#endif
        int closeSocket();
        int( *m_getError )();
        int m_port;
        std::atomic< int > m_ready;
        std::string m_instructionString;

        void receiveCommand();
        void sendResults();
    public:
        Client() : m_ready( 0 ) {}
        Client( const Client& orig ) {}
        virtual ~Client() {}
        void run( int );
        int connectionReady();
        void signalEnd();
        std::string& getInstructionString();
    };
}

#endif
