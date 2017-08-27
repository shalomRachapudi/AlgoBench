#include "GraphStructure.h"

namespace inf2b {

void Vertex::addDelay() 
{
    // add an infinitesimally small pause, since graph generation duration
    // swamps execution time and even with large graphs (> 1,000,000 vertices),
    // search is still in the ms range while the former is in seconds
    std::this_thread::sleep_for( std::chrono::microseconds( 1 ) ); // unreliable
}

// using smart pointers here would complicate matters unnecessarily: cycles will
// be created with shared_ptr, necessitating introducing weak-ptr somewhere, and 
// then converting the latter to shared_ptr to access the pointed-to object
bool Vertex::addNeighbour( Vertex* pV )
{
    // make sure the neighbour hasn't been added before
    if (std::find_if( m_adjPtrList.begin(), m_adjPtrList.end(), [&pV] ( const Vertex* p ) { return pV == p; } ) == m_adjPtrList.end() ) {
        //std::cout << "pVertex value: " << pV->value << " (Mem loc: " << pV << ")\n";
        m_adjPtrList.push_back( std::move( pV ) );
        return true;
    }
    return false;
}

const size_t Vertex::getValue()
{
    return m_value;
}

int Vertex::getState() 
{
    return m_state;
}

/**
 * This method simulates a slightly "heavier" vertex visit if isDelayed is true. This is included because
 * the algo runs so fast that it takes too long to generate the structure, compared to running
 * DFS or BFS.
 */
 void Vertex::setState(int s)
 {
     if ( m_isDelayed ) {
         addDelay();
    }
    m_state = s;
}

void Vertex::resetNeighbours() 
{
    m_adjPtrList.resize( 0 );
    // using vector::clear() will NOT reset capacity, only size,
    // which might be a good thing if we still will allocate more
}

VertexPtrList& Vertex::getNeighbours() 
{
    // this is an edge visit - make it "heavier" if requested by user
    if ( m_isDelayed ) {
        addDelay();
    }
    return m_adjPtrList;
}

void Graph::addVertex( size_t key )
{
    if ( key >= m_numVertices ) {
        throw std::string( "Assigning values beyond graph size." );
    }
    m_vertices.emplace_back( key, m_isDelayed );
}

VertexList& Graph::getVertices()
{
    return m_vertices;
}

// array operator
Vertex& Graph::operator[]( size_t index )
{
    return m_vertices[ index ];
}

// reserves space of newSize in vector, sets size, and returns former size
size_t Graph::setNumVertices( size_t newSize ) 
{
    m_vertices.reserve( newSize );
    auto tmp = m_numVertices;
    m_numVertices = newSize;
    return tmp;
}

size_t Graph::getNumVertices()
{
    return m_numVertices;
}

void Graph::resetEdges() 
{
    for ( auto& v : m_vertices ) {
        v.resetNeighbours();
    }
}

void Graph::printVertices() 
{
    for ( auto& v : m_vertices ) {
        std::cout << "V: " << v.getValue() << "; S: " << v.getState() << " M: " << &v << std::endl;
    }
}

void Graph::printGraph() 
{
    for ( auto& v : m_vertices ) {
        std::cout << "Vertex " << v.getValue() << "\tState: " << v.getState() << "\tNeigbours: ";
        for ( auto& n : v.getNeighbours() ) {
            std::cout << n->getValue() << ", ";
        }
        std::cout << std::endl;
    }
}

}
