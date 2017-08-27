cmake_minimum_required (VERSION 2.8.12 FATAL_ERROR)
project (AlgoBench_B)

# load windows libraries
if(WIN32 OR WIN64)
    find_library(WSOCK32_LIBRARY wsock32)
    find_library(WS2_32_LIBRARY ws2_32)
    find_library(k32_LIBRARY Kernel32)
    message(${WSOCK32_LIBRARY})
    message(${WS2_32_LIBRARY})
    message(${k32_LIBRARY})
endif()

set (AlgoBench_B_HEADERS Algorithm.h BinarySearchTree.h Client.h Generator.h GraphStructure.h Heapsort.h LinearSearch.h Semaphore.h stdafx.h AlgoTimer.h ClientPipe.h global.h HashBucketStructure.h InsertionSort.h predef.h Server.h targetver.h BinarySearch.h ExternalMergeSort.h GraphAlgo.h Hashing.h InternalMergeSort.h Quicksort.h ServerPipe.h)

set (AlgoBench_B_SOURCES Algorithm.cpp BinarySearchTree.cpp Client.cpp ExternalMergeSort.cpp global.cpp GraphStructure.cpp Hashing.cpp InsertionSort.cpp LinearSearch.cpp Quicksort.cpp ServerPipe.cpp BinarySearch.cpp ClientPipe.cpp Generator.cpp GraphAlgo.cpp HashBucketStructure.cpp Heapsort.cpp InternalMergeSort.cpp main.cpp Server.cpp stdafx.cpp)

set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

add_executable (Algobench_Backend ${AlgoBench_B_SOURCES} ${AlgoBench_B_HEADERS})

if(WIN32 OR WIN64)
	target_link_libraries(Algobench_Backend pthread ${WS2_32_LIBRARY} ${WSOCK32_LIBRARY} ${k32_LIBRARY})
else()
	target_link_libraries(Algobench_Backend pthread)
endif()
