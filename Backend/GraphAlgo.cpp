#include "GraphAlgo.h"
//using namespace inf2b;

namespace inf2b {
void GraphAlgo::BFS::operator()()
{
    std::cout << "Executing BFS..." << std::endl;
    auto size = m_graph.getNumVertices();
    int treeCount = 0;
    for ( auto& v : m_graph.getVertices() ) {
        if ( v.getState() == s_colour.white ) {
            bfsFromVertex( v );
            ++treeCount;
        }
    }
    std::cout << "Number of trees in graph forest: " << treeCount << "\n=============================" << std::endl;
}

void GraphAlgo::BFS::bfsFromVertex( Vertex& v ) 
{
    v.setState( s_colour.black );
    // reset for each tree: first node touched here
    m_counter = 1;
    std::queue< Vertex* > vQueue;
    vQueue.push( &v );
    while ( !vQueue.empty() ) {
        // get a pointer to the first element
        auto& pV = vQueue.front();
        vQueue.pop();
        for ( auto& v : pV->getNeighbours() ) {
            if ( v->getState() == s_colour.white ) {
                v->setState( s_colour.black );
                vQueue.push( std::move( v ) );
                //// count how many nodes touched
                //++counter;
                //if (counter > longestPath) {
                //    longestPath;
                //}
            }
        }
    }
}

void GraphAlgo::DFS::operator()()
{
    std::cout << "Executing DFS..." << std::endl;
    auto size = m_graph.getNumVertices();
    int treeCount = 0;
    for ( auto& v : m_graph.getVertices() ) {
        if ( v.getState() == s_colour.white ) {
            ++treeCount;
            dfsFromVertex( v );
        }
    }
    std::cout << "Number of trees in graph forest: " << treeCount << "\n=============================" << std::endl;
}

void GraphAlgo::DFS::dfsFromVertex( Vertex& v )
{
    std::stack< Vertex* > vStack;
    vStack.push( &v );
    while ( !vStack.empty() ) {
        // get a pointer to the first element
        auto pV = vStack.top();
        vStack.pop();
        if ( pV->getState() == s_colour.white ) {
            // count how many nodes touched
            ++m_counter;
            pV->setState( s_colour.black );
            
            for ( auto& v : pV->getNeighbours() ) {
                vStack.push( std::move( v ) );
            }
        }
    }
}
}
