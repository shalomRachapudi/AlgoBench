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
#pragma once
#include "stdafx.h"

namespace inf2b
{
    /**
    * Much of the code here is courtesy of Nick Athanasiou
    * https://ngathanasiou.wordpress.com/2015/04/01/benchmarking-in-c/
    */
    //template<typename TimeT = std::chrono::milliseconds>
    template<typename TimeT>
    struct AlgoTimer {
        template<typename F, typename... Args>
        static typename TimeT::rep execution(F func, Args&&... args) {
            auto start = std::chrono::steady_clock::now();
            func(std::forward<Args>(args)...);
            auto duration = std::chrono::duration_cast<TimeT>(
                std::chrono::steady_clock::now() - start);
            return duration.count();
        }

        template<typename F, typename ...Args>
        static auto duration(F func, Args&&... args) -> TimeT{
            auto start = std::chrono::steady_clock::now();
            func(std::forward<Args>(args)...);
            return std::chrono::duration_cast<TimeT>(std::chrono::steady_clock::now() - start);
        }

        // the actual function being used in this project
        template<typename T, typename ...Args>
        static auto timedExecution(Args&&... args)-> TimeT {
            T t(std::forward<Args>(args)...);
            auto start = std::chrono::steady_clock::now();
            
            std::cout << "[STATUS]\tExecuting algorithm" << std::endl;
            //excuting selected algorithm
            t();
            return std::chrono::duration_cast<TimeT>(std::chrono::steady_clock::now() - start);
        }
    };
}
