#include "Heapsort.h"
using namespace inf2b;

void Heapsort::buildHeap()
{
    // start from the rightmost h-1 level node
    std::cout << "[UPDATE]\tBulding heap..." << std::endl;
    std::cout << "[TASKRUNNER] Bulding heap..." << std::endl;
    int height = log2( m_input.size() );
    std::cout << "[TREEHEIGHT]\t" << height << std::endl;
    long startNodeIndex = ( int ) pow( 2.0, height ) - 2;
    do {
        // heapify this subtree
        heapify( startNodeIndex );
    } while ( --startNodeIndex >= 0 );
}

void Heapsort::hsort() 
{   
    std::cout << "[UPDATE]\tSorting..." << std::endl;
    std::cout << "[TASKRUNNER] Executing Heap sort..." << std::endl;
    while ( m_cursor < ( m_input.size() - 1 ) ) {
        // exchange first and last elements
        swap( 0, m_input.size() - m_cursor - 1 );
        ++m_cursor;
        heapify( 0 );
    }

    int prev = 0;
    for ( auto n : m_input ) {
        assert( prev <= n );
        prev = n;
    }
}

void Heapsort::heapify( long nodeIndex )
{
    long maxValue = nodeIndex;
    long left = getLeftChild( nodeIndex );
    long right = getRightChild( nodeIndex );
    if ( hasLeftChild( nodeIndex ) && m_input[ left ] > m_input[ nodeIndex ] ) {
        maxValue = left;
    }
    if ( hasRightChild( nodeIndex ) && m_input[ right ] > m_input[ maxValue ] ) {
        maxValue = right;
    }   
    if ( nodeIndex != maxValue ) {
        swap( nodeIndex, maxValue );
        heapify( maxValue );
    }
}

bool Heapsort::hasLeftChild( long nodeIndex )  
{
    return ( getLeftChild( nodeIndex ) < ( m_input.size() - m_cursor ) );
}

bool Heapsort::hasRightChild( long nodeIndex ) 
{
    return (getRightChild( nodeIndex ) < ( m_input.size() - m_cursor ) );
}

long Heapsort::getLeftChild( long index )
{
    // multiply by 2
    return ( ( index << 1 ) + 1 );
}

long Heapsort::getRightChild( long index )
{
    return ( ( index << 1 ) + 2 );
}

long Heapsort::getParent( long index ) 
{
    return ( ( index - 1 ) >> 1 );
}

void Heapsort::swap( long indexX, long indexY ) 
{
    auto temp = m_input[ indexX ];
    m_input[ indexX ] = m_input[ indexY ];
    m_input[ indexY ] = temp;
}

void Heapsort::operator()()
{
    buildHeap();
    hsort();
};
