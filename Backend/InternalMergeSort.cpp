/* 
 * File:   InternalMergeSort.cpp
 * Author: Yufen Wang
 */

#include "InternalMergeSort.h"
using namespace inf2b;

void InternalMergeSort::merge( int head, int middle, int rear )
{
    int i = 0, j = 0, k = head;
    InputVectorType left, right;
    
    for ( int i = head; i <= middle; i++)
        left.push_back( m_input.at( i ) );
    
    for ( int i = middle + 1; i <= rear; i++ )
        right.push_back( m_input.at( i ) );
    
    //compare left and right array
    while ( i < left.size() && j < right.size() ) { 
        if ( left[ i ] <= right[ j ] )
            m_input[ k++ ] = left[ i++ ];
        else
            m_input[ k++ ] = right[ j++ ];
    }
    
    for ( ; i < left.size(); i++ ) 
        m_input[ k++ ] = left[ i ];
    for ( ; j < right.size(); j++ ) 
        m_input[ k++ ] = right[ j ];
}

void InternalMergeSort::mergeSort( int head, int rear )
{
    int middle;
    if ( head < rear )
    {
        middle = ( head + rear ) / 2;      //split input into two parts
        mergeSort( head, middle );   //recursive left half
        mergeSort( middle + 1, rear ); //right half
        merge( head, middle, rear );    //merge the two half
    }
}

InputVectorType InternalMergeSort::operator()()
{
    if( !m_debug ) {
        std::cout << "[TASKRUNNER] Executing Internal Mergesort..." << std::endl;
    }
    
    mergeSort( 0, m_input.size() - 1 );
    return m_input;
}
