#include "Generator.h"
//using namespace inf2b;

namespace inf2b 
{
    
void Generator::generateRandom( InputVectorType& input, const size_t& size, InputIntType min, InputIntType max )
{
    std::uniform_int_distribution< InputIntType > range( min, max );
    // Mersenne Twister generator engine
    std::mt19937 randEngine;

    for ( size_t i = 0; i < size; ++i ) {
        input.push_back( ( InputIntType ) ( min + range( randEngine ) * ( max - min ) ) );
        // std::cout << input[i] << std::endl;
    }
}

void Generator::generateRepeated( InputVectorType& input, const size_t& size, int thId )
{
    std::cout << "Generating REPEATED input. Size: " << size << std::endl;

    InputIntType number = rand();
    for ( size_t i = 0; i < size; ++i ) {
        input.push_back( number );
    }
}

void Generator::generateSorted( InputVectorType& input, const size_t& size, InputIntType min, int thId )
{ 
    std::cout << "Generating SORTED input. Size: " << size << std::endl;
    for ( auto i = min; i < (min + size); ++i ) {
        input.push_back( i );
    }
}

void Generator::generateRSorted( InputVectorType& input, const size_t& size, InputIntType max, int thId )
{ 
    std::cout << "Generating REVERSE-SORTED input. Size: " << size << std::endl;
    if ( max < size ) {
        max = size;
    }
    
    for ( auto i = max; i > ( max - size ); --i ) {
        input.push_back( i );
    }
}

void Generator::generateWorstRecursive( InputVectorType& input, long low, long high, long leastValue )
{ 
    if ( low < high ) {
        generateWorstRecursive( input, low + 1, high, leastValue + 1 );
    }
    long pivotIndex = ( low + high ) / 2;
    input[ low ] = input[ pivotIndex ];
    input[ pivotIndex ] = leastValue;
}

void Generator::generateWorst( InputVectorType& input, long low, long high, long leastValue )
{ 
    std::cout << "Generating WORST-CASE input (centred pivot). Size: " << input.size() << std::endl;
    low = high;
    InputIntType maxValue = leastValue + input.size() - 1;
    InputIntType pivotIndex;
    
    input[ low ] = maxValue;
    while ( --low >= 0 ) {
        pivotIndex = ( low + high ) / 2;
        input[ low ] = input[ pivotIndex ];
        input[ pivotIndex ] = --maxValue;
    }
}

void Generator::generateArray( InputVectorType& input, const size_t inputSize, const int inputDistribution, const int runNumber,
            InputIntType minValue, InputIntType maxValue )
{

    if ( inputSize <= 0 ) {
        return;
    }
    
    input.reserve( inputSize );

    switch ( inputDistribution ) {
        case 1: // random
            std::cout << "[UPDATE]\tRun " << (runNumber + 1) << ": Generating RANDOM input: Size=" << inputSize << std::endl;
            generateRandom( input, inputSize, minValue, maxValue );
            break;
        case 2: // repeated
            std::cout << "[UPDATE]\tRun " << (runNumber + 1) << ": Generating REPEATED input: Size=" << inputSize << std::endl;
            generateRepeated( input, inputSize );
            break;
        case 3: // sorted
            std::cout << "[UPDATE]\tRun " << (runNumber + 1) << ": Generating SORTED input: Size=" << inputSize << std::endl;
            generateSorted( input, inputSize, 0 );
            break;
        case 4: // r-sorted
            std::cout << "[UPDATE]\tRun " << (runNumber + 1) << ": Generating REVERSE-SORTED input: Size=" << inputSize << std::endl;
            generateRSorted( input, inputSize, inputSize );
            break;
        case 5: // worst-case
            std::cout << "[UPDATE]\tRun " << (runNumber + 1) << ": Generating WORST-CASE input: Size=" << inputSize << std::endl;
            input.resize( inputSize );
            if ( Command[ "QUICKSORT-PIVOT-POSITION" ] == QUICKSORT_PIVOT_CENTRE && Command[ "ALGORITHM" ] == QUICK_SORT ) {
                generateWorst( input, 0, inputSize - 1, 1 );
            }
            else if ( Command[ "ALGORITHM" ] == QUICK_SORT ) {
                generateSorted( input, inputSize, 0 );
            }
            else { // no more necessary cos only Quicksort in frontend can request for worst-case
                generateRandom( input, inputSize, minValue, maxValue );
            }
            break;
        default:
            std::cout << "[UPDATE]\tRun " << (runNumber + 1) << ": Generating UNKNOWN input: Size=" << inputSize << std::endl;
            std::cout << "SIZE_MAX: " << SIZE_MAX << std::endl;
            generateRandom( input, inputSize, 0, SIZE_MAX );
            break;
    }
}

// isDelayed generates the vertices with the option to simulate a longer edge and vertex visit
// in order to make the graph algos run slower, so that we can get more significant runtimes.
// Without it, graph generation time is several times execution time, needing really large graphs
// to get any sensible runtimes.
void Generator::generateGraph( Graph& graph, const size_t& size, size_t fixedParamSize, bool directed, bool allowSelfLoop, bool fixedEdges )
{
    size_t prevSize;
    size_t numEdges;
    //bool isInitialized = false;
    if ( fixedEdges ) {
        // vertices will be incremented each time, with fixed number of edges.
        // just create the extra at each iteration
        prevSize = graph.setNumVertices( size );
        std::cout << "Generating VERTICES... Size: " << prevSize + size << std::endl;
        for ( auto i = prevSize; i < graph.getNumVertices(); ++i ) {
            graph.addVertex( i );
        }
        
        // set fixed number of edges
        numEdges = fixedParamSize;
        // memory
        MemoryUsage += sizeof( Vertex ) * graph.getNumVertices();
    }
    else {// fixed number of vertices
        if ( fixedParamSize == 0 ) {
            throw std::string( "Graph must have at least one vertex!" );
        }
        // check if the fixed vertices have not been initialized before
        if ( graph.getNumVertices() == 0 ) {
            std::cout << "Generating VERTICES... Size: " << fixedParamSize << std::endl;
            graph.setNumVertices( fixedParamSize );
            // create the vertices
            for ( auto i = 0; i < graph.getNumVertices(); ++i ) {
                graph.addVertex( i );
            }
        }
        
        // set new number of edges
        numEdges = size;
        
        // memory
        MemoryUsage += sizeof( Vertex ) * fixedParamSize;
    }
    
    std::cout << "Generating EDGES... Size: " << numEdges << std::endl;
    // ===== (re)create edges linking vertices =====
    // NB: this happens everytime, whether edges are fixed or vertices are fixed,
    // to ensure fair distribution. We start by resetting edges
    graph.resetEdges();
    // get the random number generator
    std::uniform_int_distribution< InputIntType > range( 0, static_cast< InputIntType > ( graph.getNumVertices() - 1 ) );
    std::mt19937 randEngine;
    randEngine.seed( clock() );

    // calculate memory usage
    MemoryUsage += sizeof( Vertex* ) * numEdges;
    if ( !directed ) { // add another set to cover the back reference r/ship
        MemoryUsage += sizeof( Vertex* ) * numEdges;
    }
    
    // continue execution
    while ( numEdges > 0 ) {
        // get random vertex to add neighbour to
        auto vIndex = range( randEngine );
        // choose the actual neighbour randomly
        auto nIndex = range( randEngine );
        // add them
        // check for self-loop
        if ( !allowSelfLoop && nIndex == vIndex ) {
            continue;
        }
        if ( graph[ vIndex ].addNeighbour( &graph[ nIndex ] ) ) {
            // if undirected graph, add the other way round too, cos
            // neighbour of a node must also reference that node
            if ( !directed ) {
                graph[ nIndex ].addNeighbour( &graph[ vIndex ] );
            }
            --numEdges;
        }
    }

    // to help with getting the amount of memory used
    // TODO: review
    /*size_t capacity { };
    size_t vsize { };
    for (auto& v : graph.getVertices()) {
        capacity += v.getNeighbours().capacity();
        vsize += v.getNeighbours().size();
    }
    auto mem1 = sizeof(Vertex*) * capacity;
    auto mem2 = sizeof(Vertex*) * vsize;*/
}

void Generator::generateHashList( PairList& list, const size_t numEntries, const int hashKeyType, const std::string& filename )
{
    if ( filename.length() > 0 ) {
        std::cout << "Parsing input HASH list... " << std::endl;
        // read file supplied with records as <K,V> pairs, with K and V separated
        // by tabs, and records by newline
        try {
            std::string line;
            std::ifstream inputStream( filename );
            size_t pos;
            while (std::getline( inputStream, line, ',' ) ) {
                pos = line.find( ":" );
                if ( pos == std::string::npos || pos == 0 ) {
                    //bad record - skip
                    std::cout << "[BAD RECORD SKIPPED] {" << line << "}\n";
                    continue;
                }
                list.emplace_back( line.substr( 0, pos ), line.substr( pos + 1, line.length() ) );
            }
        }
        catch ( const std::exception& ex ) {
            std::cout << "[TASKRUNNER:ERROR] (Exception) " << ex.what() << std::endl;
            exit( 1 );
        }
    }
    else {
        std::cout << "Generating HASH list... Size: " << numEntries << std::endl;
        // generate string keys and assign values
        const std::string alphaPool( "ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
        int index = 0;
        int poolSize = alphaPool.length();
        std::string key;
        std::vector< int > indices( 0 );
        // use string (alpha) keys 
        if ( hashKeyType == HASH_KEY_ALPHABET ) {
            for ( auto i = 0; i < numEntries; ++i ) {
                if ( i % poolSize == 0 && i >= poolSize ) {
                    key.clear();
                    for ( int j = indices.size() - 1; j >= 0; --j ) {
                        key.push_back( alphaPool[ indices[ j ] ] );
                    }
                    for ( auto j = 0; j < indices.size(); ++j ) {
                        ++indices[ j ];
                        if ( indices[ j ] % poolSize == 0 ) {
                            indices[ j ] = 0;
                            if ( j == indices.size() - 1 ) { // add one more digit
                                indices.push_back( 0 );
                                break;
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
                list.emplace_back( key + alphaPool[ i % poolSize ], std::move( std::to_string( i ) ) );
            }
        }
        else { // use number keys
            for ( auto i = 0; i < numEntries; ++i ) {
                std::string key( std::to_string( i ) );
                std::string value( std::to_string( i ) );
                list.emplace_back( std::move( key ), std::move( value ) );
            }
        }
    }
}

/*
void Generator::generateWorstCaseInput(InputVectorType& input, InputIntType min, int thId = 0) 
{
    long count = size;
    long pivotIndex, mid;
    bool isEven = (count + 1) % 2 ? 1 : 0;
    long low = -1,
    high = count - 1,
    lhsStartIndex = isEven ? 0 : 1,
    lhsIndex = lhsStartIndex,
    lhsLevel = 0,
    stepSize = 2;

    while (++low < high) {
        pivotIndex = (high + low) / 2;
        // initialize lowest element at pivot
        if (low == 0) {
            mid = pivotIndex;
            input[mid] = min;
        }
        // assign the next lowest elements
        if ((high - low) % 2 == 1) {
            // even number of elems in sub array, therefore next lowest element
            // should appear to the immediate right of current min element
            input[pivotIndex + 1] = ++min;
        }
        else { // odd number of elems in sub array
            if (low < mid) {
            // first pass through lhs, no use of swapped elements yet
                input[low] = ++min;
            lhsIndex = low;
            }
            else {
                // we've gone through once, picking elems at intervals of 2 indexes
                // restart from the least index that is empty, and move at intervals
                // of 2
                if (lhsLevel == 0) {
                    ++lhsLevel;
                    if ((high - low) % 2 == 0) {// next array size is odd
                    // we need to start at 2
                            lhsIndex = (lhsIndex + stepSize) % low + 1;
                    }
                    else {
                        lhsIndex = ((lhsIndex + stepSize) % low);
                    }
                        stepSize = pow(2.0, lhsLevel + 1);
                }
                if (lhsIndex >= mid) {
                    // reset the starting index
                    // for even count, it restarts first at 0 and increases by 4
                    // till >= middle index
                    // then at 2, then 6, then 14, then 30, etc
                    if ((high - low) % 2 == 1) {// next array size is odd
                        // we need to start at 2
                        lhsIndex = (lhsIndex + stepSize) % low + 1;
                    }
                    else {
                        lhsIndex = ((lhsIndex + stepSize) % low);
                    }
                
                    lhsStartIndex += stepSize / 2;
                    ++lhsLevel;
                    stepSize = pow(2.0, lhsLevel + 1);
                    // lhsIndex = lhsStartIndex;
                }
                input[lhsIndex] = ++min;
                lhsIndex += stepSize;
            }
        }
        
        for (auto e : input) {
            std::cout << e << ",";
        }
        
        std::cout << std::endl;
    }
} */

void Generator::generateRandomLong( long& target, long min, long max )
{
    std::uniform_int_distribution< long > range( min, max );  
    std::mt19937 randEngine;
    target = range( randEngine );
}
        
void Generator::generateRandomKey( InputIntType& target, InputIntType min, InputIntType max )
{
    std::uniform_int_distribution< InputIntType > range( min, max );  
    std::mt19937 randEngine;
    target = range( randEngine );
}

void Generator::generateSearchKey( InputVectorType input, InputIntType& key, const int searchKeyType, const int runNumber, InputIntType minValue, InputIntType maxValue )
{
    long index;
    switch ( searchKeyType ) {
        case 1://always in
            std::cout << "[UPDATE]\tRun " << ( runNumber + 1 ) << ": Generating ALWAYS-IN search key" << std::endl;
            generateRandomLong( index, 0, input.size() - 1 );
            key = input.at( index );
            break;
        case 2://not in
            std::cout << "[UPDATE]\tRun " << ( runNumber + 1 ) << ": Generating NOT-IN search target" << std::endl;
            key = 100000005;
            break;
        case 3://random
            std::cout << "[UPDATE]\tRun " << ( runNumber + 1 ) << ": Generating RANDOM search target" << std::endl;
            generateRandomKey( key, minValue, maxValue );
            break;
        default:
            std::cout << "[UPDATE]\tRun " << ( runNumber + 1 ) << ": generateSearchKey default" << std::endl;
            break;
    }
}

void Generator::generateHashInput( InputVectorType& input, const size_t numEntries, const int hashKeyType, const std::string& filename )
{
    if ( filename.length() > 0 ) {
        std::cout << "Parsing HASH input file... " << std::endl;
        try {
            std::string number;
            std::ifstream inputStream( filename );
            while ( std::getline( inputStream, number, ',' ) ) {
                input.push_back( ( size_t ) std::stoul( number ) );
            }
        }
        catch ( const std::exception& ex ) {
            std::cout << "[TASKRUNNER:ERROR] (Exception) " << ex.what() << std::endl;
            exit( 1 );
        }
    }
    else {
        std::cout << "Generating HASH input... Size: " << numEntries << std::endl;
        for ( auto i = 0; i < numEntries; ++i ) {
            input.push_back( ( InputIntType ) i );
        }
    }
}


} // namespace
