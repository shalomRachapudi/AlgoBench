#include "HashBucketStructure.h"
using namespace inf2b;


void HashBucketStructure::addEntry( size_t hashValue, std::pair<std::string, std::string>&& entry )
{
    auto i = hashValue % m_arraySize;
    m_hashBucket[ i ].insert( std::move( entry ) );
}

void HashBucketStructure::addEntry( size_t hashValue, std::pair<std::string, std::string>& entry )
{
    auto i = hashValue % m_arraySize;
    m_hashBucket[ i ].insert( std::move( entry ) );
}

size_t HashBucketStructure::size()
{
    return m_arraySize;
}

void HashBucketStructure::printBucket( std::ostream& outputStream )
{
    size_t i = 0;
    size_t minSize = m_numElements + 1;
    size_t maxSize = 0;
    
    outputStream << "BUCKET\tSIZE\tELEMENTS (K:V)\n";
    for ( auto& dict : m_hashBucket ) {
        auto s = dict.size();
        if ( s > maxSize ) {
            maxSize = s;
        }
        if ( s < minSize ) {
            minSize = s;
        }
        outputStream << i << "\t" << s << "\t";
        for ( auto& e : dict ) {
            outputStream << e.first << ":" << e.second << ",";
        }
        // the space is a placeholder so that splitting on \t still works well
        // for empty buckets
        outputStream << " \n";
        ++i;
    }
    std::cout << "[MINBUCKETSIZE]\t" << minSize;
    std::cout << "\n[MAXBUCKETSIZE]\t" << maxSize << std::endl;
    outputStream.flush();
}
