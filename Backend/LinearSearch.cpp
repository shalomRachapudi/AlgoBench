#include "LinearSearch.h"
using namespace inf2b;

void LinearSearch::linearSearch()
{
    long index = 0;
    for( InputIntType element : m_input ) {
        index++;
        if( element == m_key ){
            std::cout << "[UPDATE]\tFIND KEY " << m_key <<"AT INDEX " << ( index - 1 ) << std::endl;
            return;
        }
    }
}


void LinearSearch::operator()() 
{
    std::cout << "[TASKRUNNER] Executing linear search..." << std::endl;
    linearSearch();
}
