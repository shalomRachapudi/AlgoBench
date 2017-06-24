#include "Hashing.h"
using namespace inf2b;

void Hashing::printResults()
{
    std::stringstream ss;
    auto t = time( NULL );
    m_outputFile.open( "output/hashing" + std::to_string(t) + ".txt" );
    m_bucket.printBucket( m_outputFile );
    //bucket.printBucket(ss);
    //output = ss.str();
}

size_t Hashing::defaultGoodHash( const std::string& key ) 
{
    size_t hashValue = 0;
    for ( char c : key ) {
        hashValue = 31 * hashValue + c;
    }
    return hashValue;
}

size_t Hashing::defaultBadHash( const std::string& key ) 
{
    std::hash< unsigned char > hashFunc;
    return hashFunc( key[ 0 ] );
}

void Hashing::operator()() 
{
    std::cout << "[TASKRUNNER] Hashing..." << std::endl;
    //if (hashFunction == NULL) {
    // throws an error. deal with it later
    // hashFunction = isGoodHash ? &Hashing::defaultGoodHash : &Hashing::defaultBadHash;
    //}
    bool good = m_hashType == GOOD_HASH;
    // keep count
    size_t  count( 0 );
    for ( auto e : m_input ) {
        auto hash = good ? defaultGoodHash( e.first ) : defaultBadHash( e.first );
        m_bucket.addEntry( hash, e );
    }
    std::cout << "[TASKRUNNER] Printing results..." << std::endl;
    printResults();
}
