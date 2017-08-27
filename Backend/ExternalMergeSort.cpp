#include "ExternalMergeSort.h"
using namespace inf2b;

//print a vector for debug process, never called in normal process.
void ExternalMergeSort::printVector( InputVectorType v )
{
    for ( int i = 0; i < v.size(); i++ ) {
        std::cout << v.at( i ) << " ";
    }
    std::cout << std::endl;
}

void ExternalMergeSort::sort_block()
{
    for ( int i = 0; i < m_input.size(); i++ ) {
        m_RAM.push_back( m_input.at( i ) );
    
        if ( m_RAM.size() == m_numLimit || i == m_input.size() - 1 ) {
            m_blockNumber++;
            
            m_tmpRAM.resize( m_RAM.size() );
            mergeSort( m_RAM, m_tmpRAM, 0, m_RAM.size() - 1 );
                        
            m_tempFile[ 0 ].insert( m_tempFile[ 0 ].end(), m_RAM.begin(), m_RAM.end() );
        }
        m_RAM.clear();
        m_tmpRAM.clear();
    }
    m_RAM.clear();
    m_fileToSort = 0;
    m_fileSorted = 1;
}

void ExternalMergeSort::mergeSort( InputVectorType& RAM, InputVectorType& tmpRAM, int left, int right )
{
    if ( right <= left ) 
        return;
    
    int center = left + (right - left) / 2;
    mergeSort( RAM, tmpRAM, left, center );
    mergeSort( RAM, tmpRAM, center + 1, right );
    merge( RAM, tmpRAM, left, center, right );
}

void ExternalMergeSort::merge( InputVectorType& RAM, InputVectorType& tmpRAM, int left, int center, int right )
{
    // copy to tmpRAM[]
    for ( int temp = left; temp <= right; temp++ ) {
        tmpRAM[ temp ] = RAM[ temp ];
    }
    
    // merge back 
    int i = left, j = center + 1;
    for ( int k = left; k <= right; k++ ) {
        if ( i > center )
            RAM[ k ] = tmpRAM[ j++ ];
        else if ( j > right )
            RAM[ k ] = tmpRAM[ i++ ];
        else if ( tmpRAM[ j ] < tmpRAM[ i ] )
            RAM[ k ] = tmpRAM[ j++ ];
        else
            RAM[ k ] = tmpRAM[ i++ ];
    }
}

void ExternalMergeSort::merge_RAM()
{
    m_RAM_C.clear();
    InputVectorType::iterator iter_A = m_RAM_A.begin();
    InputVectorType::iterator iter_B = m_RAM_B.begin();
    
    while ( ( iter_A != m_RAM_A.end() ) && ( iter_B != m_RAM_B.end() ) ) 
    {
        if ( *iter_A < *iter_B ) {
            m_RAM_C.push_back( *iter_A );
            iter_A++;
        }
        else {
            m_RAM_C.push_back( *iter_B );
            iter_B++;
        }
        if ( m_RAM_C.size() == m_numLimit_C ) {
            m_tempFile[ m_fileSorted ].insert( m_tempFile[ m_fileSorted ].end(), m_RAM_C.begin(), m_RAM_C.end() );
            m_RAM_C.clear();
        }
    }
    
    if ( !m_RAM_C.empty() ) {
        m_tempFile[ m_fileSorted ].insert( m_tempFile[ m_fileSorted ].end(), m_RAM_C.begin(), m_RAM_C.end() );
        m_RAM_C.clear();
    }
    
    //erase sorted part
    m_RAM_A.erase( m_RAM_A.begin(), iter_A );
    m_RAM_B.erase( m_RAM_B.begin(), iter_B );
}

void ExternalMergeSort::merge_block( int i )
{
    if ( i == m_blockNumber - 1 ) {
        m_tempFile[ m_fileSorted ].insert( m_tempFile[m_fileSorted].end(),
                                           m_tempFile[m_fileToSort].begin() + ( i * m_blockSize ),
                                           m_tempFile[m_fileToSort].end()
                                         );
        return;
    }
    
    InputVectorType array1( m_tempFile[ m_fileToSort ].begin() + (i * m_blockSize ), 
                            m_tempFile[ m_fileToSort ].begin() + (i + 1) * m_blockSize
                          );
    InputVectorType array2;
    
    if ( i == m_blockNumber - 2 ) {
        array2.insert( array2.end(),
                       m_tempFile[ m_fileToSort ].begin() + ( i + 1 ) * m_blockSize,  
                       m_tempFile[ m_fileToSort ].end()
                     );
    }
    else {
        array2.insert( array2.end(),
                       m_tempFile[ m_fileToSort ].begin() + ( i + 1 ) * m_blockSize, 
                       m_tempFile[ m_fileToSort ].begin() + ( i + 2 ) * m_blockSize
                     );
    }
    
    InputVectorType::iterator iter_A = array1.begin();
    InputVectorType::iterator iter_B = array2.begin();
            
    while ( ( iter_A != array1.end() && m_RAM_A.empty() ) || ( iter_B != array2.end() && m_RAM_B.empty() ) ) 
    {
        while ( iter_A != array1.end() && m_RAM_A.size() < m_numLimit_A ) {
            m_RAM_A.push_back( *iter_A );
            iter_A++;
        }
        
        while ( iter_B != array2.end() && m_RAM_B.size() < m_numLimit_B ) {
            m_RAM_B.push_back( *iter_B );
            iter_B++;
        }
        merge_RAM();
    }
    
    if ( !m_RAM_A.empty() ) {
        m_tempFile[ m_fileSorted ].insert( m_tempFile[ m_fileSorted ].end(), m_RAM_A.begin(), m_RAM_A.end() );
        m_RAM_A.clear();
        m_tempFile[ m_fileSorted ].insert( m_tempFile[ m_fileSorted ].end(), iter_A, array1.end() );
    }
    else if ( !m_RAM_B.empty() ) {
        m_tempFile[ m_fileSorted ].insert( m_tempFile[ m_fileSorted ].end(), m_RAM_B.begin(), m_RAM_B.end() );
        m_RAM_B.clear();
        m_tempFile[ m_fileSorted ].insert( m_tempFile[ m_fileSorted ].end(), iter_B, array2.end() );
    }
}

void ExternalMergeSort::merge()
{
    if ( m_blockNumber == 1 ) {
        m_input = m_tempFile[ m_fileToSort ];
        return;
    }
    
    m_tempFile[ m_fileSorted ].clear();
    for (int i = 0; i < m_blockNumber; i += 2 ) {
        merge_block( i );
    }
    //toggle files
    if ( m_fileSorted == 0 ) {
        m_fileSorted = 1;
        m_fileToSort = 0;
    }
    else {
        m_fileSorted = 0;
        m_fileToSort = 1;
    }          
    m_blockSize = m_blockSize * 2;
    if ( m_blockNumber % 2 == 1 ) {
        m_blockNumber = ( m_blockNumber / 2 ) + 1;
    }
    else {
        m_blockNumber = m_blockNumber / 2;
    }
    merge();
}

void ExternalMergeSort::operator()()
{
    std::cout << "[TASKRUNNER] Executing External Mergesort" << std::endl;
    
    sort_block();
    merge();
}
