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
 *
 * The entry point for the application.
 * 
 * Modified by Yufen Wang (2016), Shalom (2017)
 */

#ifndef MAIN_H
#define MAIN_H

#include "stdafx.h"
#include "global.h"
// application files
#include "Server.h"
#include "Client.h"
#include "Algorithm.h"
#include "Quicksort.h"
#include "Heapsort.h"
#include "Generator.h"
#include "AlgoTimer.h"
#include "HashBucketStructure.h"
#include "Hashing.h"
#include "InsertionSort.h"
#include "ExternalMergeSort.h"
#include "InternalMergeSort.h"
#include "LinearSearch.h"
#include "BinarySearch.h"
#include "BinarySearchTree.h"

namespace inf2b
{
    typedef std::chrono::milliseconds MilliSec;

    size_t currentInputSize;
    std::vector< clock_t > runTimes;
    std::function< void() > fRun;
    std::string inputFilename;
    std::vector< InputVectorType > inputVectorArray;
    std::string EXTERNAL_MERGESORT_RAM;


    void reportProgress( std::string output ) 
    {
        // create scope for lock_guard
            {
                // acquire lock first. Once guard leaves scope, lock is released
                std::lock_guard< std::mutex > guard( inf2b::progressSemaphore.sMutex );
                // optionally
                //inf2b::myMutex.lock();
                //std::lock_guard<std::mutex> guard(inf2b::myMutex, std::adopt_lock);
                // use std::lock(mut1, mut2, ...) to acquire in all-or-none fashion
                if ( output.length() > 0 ) {
                    // allows the send loop to skip the send block if so desired. Just pass in empty string
                    inf2b::progressReport.push_back( output );
                }
            }
        // inform the communicator that there's something to send
        inf2b::progressSemaphore.notify();
    }
    
    // execute by Command[]
    void execute()
    {
        using MilliSec = std::chrono::milliseconds;
        using MicroSec = std::chrono::microseconds;
        MilliSec duration;
        MicroSec duration_micro;
        std::string output;
        output.reserve( 100 );
        if ( Command[ "ALGORITHM-GROUP" ] == HASH ) {
            PairList pairList;
            Generator::generateHashList( pairList, Command[ "INPUT-STARTSIZE" ], Command[ "HASH-KEY-TYPE" ], inputFilename );
            Hashing hashing( pairList, Command[ "HASH-BUCKET-ARRAY-SIZE" ], Command[ "HASH-FUNCTION-TYPE" ], output );
            
            hashing();
            
            std::cout << "[TASKRUNNER] Transferring results to SERVER..." << std::endl;
            std::cout << "[STATUS]\tTransferring results to SERVER..." << std::endl;
            
            reportProgress( output );
            return;
        }
        
        // for Tree DS and Algo
        if ( Command[ "ALGORITHM-GROUP" ] == TREE ) {
            
            InputVectorType input;
            std::cout << "[STATUS]\tGenerating input" << std::endl;
            currentInputSize = Command[ "TREE-SIZE" ];
            
            Generator::generateTreeInput( input, currentInputSize, Command[ "TREE-RANGE-LOWER-LIMIT" ], Command[ "TREE-RANGE-UPPER-LIMIT" ], Command[ "TREE-TYPE" ] );
            //std::cout << "[CURRENTINPUTSIZE]\t" << currentInputSize << std::endl;
            
            // get memory usage
            size_t memPerNode = inf2b::nodeSizeWithRef();
            MemoryUsage = currentInputSize * memPerNode;
            std::cout << "[CURRENTMEMUSAGE]\t" << MemoryUsage << std::endl;
            std::cout << "[MEMPERNODE]\t" << memPerNode << " KB" << std::endl;
            
            if ( Command[ "ALGORITHM" ] == BINARY_SEARCH_TREE ) {
                BinarySearchTree bst( input,
                                      currentInputSize,
                                      Command[ "TREE-RANGE-LOWER-LIMIT" ],
                                      Command[ "TREE-RANGE-UPPER-LIMIT" ],
                                      Command[ "DATA-ELEMENT" ],
                                      Command[ "INSERT-OP" ],
                                      Command[ "SEARCH-OP" ],
                                      Command[ "DELETE-OP" ]
                                    );
                output = bst();
                
                // send progress to front-end
                reportProgress( output + "\n" );
            }
            else {
                // TODO: Implement AVL TREES
            }
            
            return;
        }
        // the other "timing" algos
        //making the first line of table chart
        switch ( Command[ "ALGORITHM-GROUP" ] ) {
            case SORT:
            {
                output = "BEGIN\nNumber of Elements";
                // check for input file
                if ( ALGO_INPUT_FILENAME.length() > 0 ) {
                    std::cout << "[STATUS]\tLoading input file..." << std::endl;
                    // std::cout << "Loading input file..." << std::endl;
                    // read file as CSV
                    std::string line;
                    std::ifstream inputStream( ALGO_INPUT_FILENAME );
                    size_t pos;
                    while ( std::getline( inputStream, line, '\n' ) ) {
                        std::istringstream lineStream( line );
                        std::string number;
                        inputVectorArray.push_back( InputVectorType() );
                        pos = inputVectorArray.size() - 1;
                        while ( std::getline( lineStream, number, ',' ) ) {
                            inputVectorArray[ pos ].push_back( ( size_t ) std::stoul( number ) );
                        }
                    }
                    Command[ "NUMREPEATS" ] = 1;
                    Command[ "NUMRUNS" ] = inputVectorArray.size();
                    std::cout << "[NUMRUNS]\t" << Command[ "NUMRUNS" ] << std::endl;
                    currentInputSize = Command[ "NUMRUNS" ] > 0 ? inputVectorArray[ 0 ].size() : 0;
                }
                break;
            }
            case GRAPH:
            {
                if ( Command[ "GRAPH-FIXED-EDGES" ] ) {
                    output = "BEGIN\nNumber of Edges";
                }
                else {
                    output = "BEGIN\nNumber of Vertices";
                }
                break;
            }
            case SEARCH:
            {
                output = "BEGIN\nNumber of Elements";
                // check for input file
                if ( ALGO_INPUT_FILENAME.length() > 0 ) {
                    std::cout << "[STATUS]\tLoading input file..." << std::endl;
                    // read file
                    std::string line;
                    std::ifstream inputStream( ALGO_INPUT_FILENAME );
                    size_t pos;
                    while ( std::getline( inputStream, line, '\n' ) ) {
                        std::istringstream lineStream(line);
                        std::string number;
                        inputVectorArray.push_back( InputVectorType () );
                        pos = inputVectorArray.size() - 1;
                        while ( std::getline( lineStream, number, ',' ) ) {
                            inputVectorArray[ pos ].push_back( ( size_t ) std::stoul( number ) );
                        }
                    }
                    Command[ "NUMREPEATS" ] = 1;
                    Command[ "NUMRUNS" ] = inputVectorArray.size();
                    std::cout << "[NUMRUNS]\t" << Command["NUMRUNS"] << std::endl;
                    currentInputSize = Command[ "NUMRUNS" ] > 0 ? inputVectorArray[ 0 ].size() - 1 : 0;
                }
                break;
            }
            default:
            {
                throw std::string( "Invalid algorithm specified!" );
            }
        }
        
        for ( int j = 0; j < Command[ "NUMREPEATS" ]; ++j ) {
            output += "\tRun " + std::to_string( j + 1 );
        }
        output += "\n";
        reportProgress( output );

        // executing algorithms
        for ( int i = 0; i < Command[ "NUMRUNS" ]; ++i ) {
            
            currentInputSize = Command[ "INPUT-STARTSIZE" ] + ( i * Command[ "INPUT-STEPSIZE" ] );
            
            long elapsedTime = 0L;
            output = std::to_string( currentInputSize );

            // repeat with same input for the specified times
            for ( int j = 0; j < Command[ "NUMREPEATS" ]; ++j ) {

                switch ( Command[ "ALGORITHM-GROUP" ] ) {
                    case SORT:
                    {
                        InputVectorType input;
                        if ( ALGO_INPUT_FILENAME.length() > 0 ) {
                            // use the already processed input
                            input = ( std::move( inputVectorArray[ i ] ) );
                            currentInputSize = input.size();
                        }
                        else {
                            std::cout << "[STATUS]\tGenerating input" << std::endl;
                            Generator::generateArray( input, currentInputSize, Command[ "INPUT-DISTRIBUTION" ], j,
                                Command[ "INPUT-MINVALUE" ], Command[ "INPUT-MAXVALUE" ] );
                        }
                        std::cout << "[CURRENTINPUTSIZE]\t" << currentInputSize << std::endl;
                        
                        // get mem usage
                        MemoryUsage = currentInputSize * sizeof(InputIntType);
                        std::cout << "[CURRENTMEMUSAGE]\t" << MemoryUsage << std::endl;
                        if ( Command[ "ALGORITHM" ] == HEAP_SORT ) {
                            duration = inf2b::AlgoTimer< MilliSec >::timedExecution< Heapsort >( input );
                        }
                        else if ( Command[ "ALGORITHM" ] == QUICK_SORT ) {
                            duration = inf2b::AlgoTimer< MilliSec >::timedExecution< Quicksort >( input, Command[ "QUICKSORT-PIVOT-POSITION" ] );
                        }
                        
                        else if ( Command[ "ALGORITHM" ] == INSERTION_SORT ) {
                            duration = inf2b::AlgoTimer< MilliSec >
                                ::timedExecution<InsertionSort>( input );
                        }
                        else if ( Command[ "ALGORITHM" ] == EXTERNAL_MERGE_SORT ) {
                            std::string s_ram = EXTERNAL_MERGESORT_RAM;
                            double ram = atof( s_ram.c_str() );
                            duration = inf2b::AlgoTimer< MilliSec >
                                ::timedExecution< ExternalMergeSort >( input, ram );
                        }
                        else if ( Command[ "ALGORITHM" ] == INTERNAL_MERGE_SORT ) {
                            duration = inf2b::AlgoTimer< MilliSec >
                                ::timedExecution< InternalMergeSort >( input );
                        }
                        break;
                    }
                    case GRAPH:
                    {
                        Graph graph( static_cast< bool >( Command[ "GRAPH-IS-DELAYED" ] ) );
                        if ( ALGO_INPUT_FILENAME.length() > 0 || false ) { // disabled for now
                            // use the already processed input
                        }
                        else {
                            std::cout << "[STATUS]\tGenerating input" << std::endl;
                            Generator::generateGraph( graph, currentInputSize, Command[ "GRAPH-FIXED-SIZE" ], Command[ "GRAPH-IS-DIRECTED" ], Command[ "GRAPH-ALLOW-SELF-LOOP" ], Command[ "GRAPH-FIXED-EDGES" ] );
                        }
                        
                        //graph.printGraph();
                        
                        std::cout << "[CURRENTINPUTSIZE]\t" << currentInputSize << std::endl;
                        auto memory = sizeof( graph );
                        memory = sizeof( Graph );
                        memory = sizeof( Vertex );
                        memory = sizeof( VertexPtrList );
                        memory = sizeof( int );
                        memory = sizeof( size_t );
                        // experimentally 1.8 is fair - the extra memory comes during execution where
                        // pointers are pushed onto the stack/queue
                        std::cout << "[CURRENTMEMUSAGE]\t" << MemoryUsage * 1.8 << std::endl;
                        MemoryUsage = 0;
                        if ( Command[ "ALGORITHM" ] == GRAPH_BFS ) {
                            duration = inf2b::AlgoTimer< MilliSec >::timedExecution< GraphAlgo::BFS >( graph );
                        }
                        else if ( Command[ "ALGORITHM" ] == GRAPH_DFS ) {
                            duration = inf2b::AlgoTimer< MilliSec >::timedExecution< GraphAlgo::DFS >( graph );
                        }
                        break;
                    }
                    case SEARCH:
                    {
                        InputVectorType input;
                        InputIntType key;
                        if ( ALGO_INPUT_FILENAME.length() > 0 ) {
                            // use the already processed input
                            key = inputVectorArray[ i ][ 0 ];
                            inputVectorArray[ i ].erase( inputVectorArray[ i ].begin() );
                            input = ( std::move( inputVectorArray[ i ] ) );
                            currentInputSize = input.size();
                        }
                        else {
                            std::cout << "[STATUS]\tGenerating input" << std::endl;
                            Generator::generateArray( input, 
                                                      currentInputSize,
                                                      Command[ "INPUT-DISTRIBUTION" ],
                                                      j,
                                                      Command[ "INPUT-MINVALUE" ],
                                                      Command[ "INPUT-MAXVALUE" ]
                                                    );
                            Generator::generateSearchKey( input, 
                                                          key,
                                                          Command[ "SEARCH-KEY-TYPE" ],
                                                          j,      
                                                          Command[ "INPUT-MINVALUE" ],
                                                          Command[ "INPUT-MAXVALUE" ]
                                                        );
                        }
                        
                        std::cout << "[CURRENTINPUTSIZE]\t" << currentInputSize << std::endl;
                        // get mem usage
                        MemoryUsage = currentInputSize * sizeof( InputIntType );
                        std::cout << "[CURRENTMEMUSAGE]\t" << MemoryUsage << std::endl;
                        if ( Command[ "ALGORITHM" ] == LINEAR_SEARCH ) {
                            duration_micro = inf2b::AlgoTimer< MicroSec >::timedExecution< LinearSearch >( input, key );
                        }
                        else if ( Command[ "ALGORITHM" ] == BINARY_SEARCH ) {
                            duration_micro = inf2b::AlgoTimer< MicroSec >::timedExecution< BinarySearch >( input, key );
                        }
                        break;
                    }
                    default:
                    {
                        throw std::string( "Invalid algorithm specified!" );
                    }
                }
                
                //std::cout << "[STATUS]\tExecuting algorithm" << std::endl;
                if ( Command[ "ALGORITHM-GROUP" ] == SEARCH ) {
                    std::cout << "[UPDATE]\tRun " << (j + 1) << " summary:  Size=" << currentInputSize << "  Time=" << duration_micro.count() << "μs" << std::endl << std::endl;
                    output.append( "\t" + std::to_string( duration_micro.count() ) );
                }
                else {
                    std::cout << "[UPDATE]\tRun " << (j + 1) << " summary:  Size=" << currentInputSize << "  Time=" << duration.count() << "ms" << std::endl << std::endl;
                    output.append( "\t" + std::to_string( duration.count() ) ); 
                }
            }

            std::cout << "[NUMCOMPLETEDRUNS]\t" << i + 1 << std::endl;
            reportProgress( output + "\n" );
        }
    }

    void signalHandler( int signal )
    {
        // end the backend
        exit( 1 );
    }

    void parseInstruction( const std::string& instruction ) 
    {
        std::stringstream ss( instruction );
        std::string line;
        size_t pos;
        while ( std::getline( ss, line, '\n' ) ) {
            pos = line.find( inf2b::INSTRUCTION_DELIM );
            auto&& key = line.substr( 0, pos );
            auto&& value = line.substr( pos + 1, line.length() );
            if ( key == "INPUT-FILENAME" ) {
                inputFilename = value;
                ALGO_INPUT_FILENAME = value;
                continue;
            }
            if ( key == "EXTERNAL-MERGESORT-RAM" ) {
                EXTERNAL_MERGESORT_RAM = value;
                continue;
            }
            Command[ key ] = ( size_t ) std::stol( value );
        }
    }
}

int main( int argc, char* argv[] )
{
    if ( argc < 3 ) {
        std::cout << "Usage: [program name] [mode] [port]\n\tmode\t{0=server|1=client}";
        return 1;
    }

    // register SIGINT to end the program when the connection closes prematurely (ie SERVER exits)
    signal( SIGINT, inf2b::signalHandler );
    // register aborts too, like segfaults, just in case that's what crashes the backend
    signal( SIGABRT, inf2b::signalHandler );

    // the program is run in a mode: client or server
    int mode = strtol( argv[1], NULL, 10 );
    int portNumber = strtol( argv[2], NULL, 10 );

    std::thread t;
    // mode = 0 -> server mode
    // mode = 1 -> client mode
    if ( mode == 0 ) {
        // run as server
        t = std::thread( &inf2b::Server::run, inf2b::Server(), portNumber );
        t.join();
    }
    else {
        // run as client
        inf2b::Client client;
        try {
            t = std::thread( &inf2b::Client::run, &client, portNumber ); // shared object. Careful!

            // wait 10 secs for instructions to be ready
            bool ready = inf2b::socketReadySemaphore.waitFor( std::chrono::milliseconds( 10000 ) );
            if ( !ready ) {
                throw std::string( "Error: Failed to get instructions from server." );
            }
            std::cout << client.getInstructionString() << std::endl;
            inf2b::parseInstruction( client.getInstructionString() );
            
            inf2b::execute();

            std::cout << "[TASKRUNNER] Task completed!\n";
        }
        catch ( const std::bad_alloc& ex ) {
            std::cout << "[TASKRUNNER:ERROR] Not enough heap memory to allocate input.\n"
                << "\tInput size: " << inf2b::currentInputSize << "\n"
                << "\tError: " << ex.what() << std::endl;
        }
        catch ( const std::exception& ex ) {
            std::cout << "[TASKRUNNER:ERROR] (Exception) " << ex.what() << std::endl;
        }
        catch ( const std::string& ex ) {
            std::cout << "[TASKRUNNER:ERROR] " << ex << std::endl;
        }
        catch ( ... ) {
            std::cout << "[TASKRUNNER:ERROR] (Generic) " << std::endl;
        }
        client.signalEnd();
        // write empty string - allows communicator to release from the blocking
        // send by notify()-ing so comm can see ready == 0
        inf2b::reportProgress( "" );
        t.join(); // just to make sure t is always initialized
    }

    std::cout << "TaskRunner exiting..." << std::endl;
    return 0;
}

#endif
