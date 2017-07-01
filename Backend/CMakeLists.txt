cmake_minimum_required (VERSION 2.8.12 FATAL_ERROR)
project (AlgoBench_B)

set (AlgoBench_B_HEADERS BinarySearchTree.h Algorithm.h Client.h Generator.h GraphStructure.h Heapsort.h LinearSearch.h Semaphore.h stdafx.h AlgoTimer.h ClientPipe.h global.h HashBucketStructure.h InsertionSort.h predef.h Server.h targetver.h BinarySearch.h ExternalMergeSort.h GraphAlgo.h Hashing.h InternalMergeSort.h Quicksort.h ServerPipe.h)

set (AlgoBench_B_SOURCES BinarySearchTree.cpp Algorithm.cpp Client.cpp ExternalMergeSort.cpp global.cpp GraphStructure.cpp Hashing.cpp InsertionSort.cpp LinearSearch.cpp Quicksort.cpp ServerPipe.cpp BinarySearch.cpp ClientPipe.cpp Generator.cpp GraphAlgo.cpp HashBucketStructure.cpp Heapsort.cpp InternalMergeSort.cpp main.cpp Server.cpp stdafx.cpp)

set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

add_executable (Algobench_Backend ${AlgoBench_B_SOURCES} ${AlgoBench_B_HEADERS})

target_link_libraries (Algobench_Backend pthread)
