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

#ifndef SEMAPHORE_H
#define SEMAPHORE_H

#pragma once
#include "stdafx.h"

// based on answers in http://stackoverflow.com/questions/4792449/c0x-has-no-semaphores-how-to-synchronize-threads

namespace inf2b
{

    class Semaphore {
    public:
        Semaphore( int count_ = 0 ) : count( count_ ) {}

        std::mutex sMutex; // allow it to be used directly outside the cv functions

        void notify() {
            std::unique_lock< std::mutex > lock( sMutex );
            ++count;
            cv.notify_one();
        }

        void wait() {
            std::unique_lock< std::mutex > lock( sMutex );
            //while ( count == 0 ) {
            cv.wait( lock, [&] { return count > 0; } );
            //    cv.wait(lock);
            //}
            --count;
        }

        bool waitFor( std::chrono::milliseconds timeout ) {
            std::unique_lock< std::mutex > lock( sMutex );
            /*
            condition_variable::wait_for(lock, timeout, predicate) works like:

            while(!Pred()){
            if(wait_for(Lck, Rel_time) == cv_status::timeout){return Pred();}
            }
            return true;

            This is used because 'spurious' notify() could be fired within the waiting period, and the
            wait would return std::cv_status::no_timeout, falsely indicating that the release was
            the result of another thread firing notify(). Hence if that occurs, recheck the 
            condition.
            */
            if ( cv.wait_for( lock, timeout, [&] { return count > 0; } ) ) {
                --count;
                return true;
            }
            return false;
        }

    private:
        std::condition_variable cv;
        int count;
    };
}

#endif
