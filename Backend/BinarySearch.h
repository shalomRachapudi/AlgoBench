/*
 * Created by Yufen Wang.
 * 2016
 */

#pragma once
#include "stdafx.h"
#include "global.h"

namespace inf2b
{
    class BinarySearch {
    private:
        InputVectorType& input;
        InputIntType& key;
        
        void bsearch(){
            long low = 0, high = input.size();
            long middle = (low + high)/2;
            while(middle<high){
                if(input.at(middle) == key){
                    std::cout<<"[UPDATE]\tFIND KEY " <<key <<" AT INDEX "<<middle <<std::endl;
                    return;
                }
                else{
                    if(input.at(middle) > key){
                        high = middle - 1;
                    }
                    else if(input.at(middle) < key){
                        low = middle + 1;
                    }
                    middle = (low + high)/2;
                }
            }
        }

    public:

        BinarySearch(InputVectorType& in, InputIntType& t) : input (in), key(t){}

        ~BinarySearch() { }

        void operator()() {
            std::cout << "[TASKRUNNER] Executing binary search..." << std::endl;
            bsearch();
        }
    };
}

