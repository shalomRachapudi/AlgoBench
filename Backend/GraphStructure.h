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
  */

#ifndef GRAPHSTRUCTURE_H
#define GRAPHSTRUCTURE_H

#pragma once
#include "stdafx.h"
#include "global.h"

namespace inf2b
{
    class Vertex;
    struct VertexColour {
        int white = 0;
        int gray = 1;
        int black = 2;
    };
    static struct VertexColour s_colour;

    typedef uint32_t InputIntType;
    typedef std::vector< InputIntType > Row;
    typedef std::vector< Row > Matrix;
    typedef std::vector< Vertex > VertexList;
    typedef std::vector< Vertex* > VertexPtrList;
        

    class Vertex 
    {
    private:
        const size_t m_value;
        int m_state;
        VertexPtrList m_adjPtrList;
        bool m_isDelayed;

        void addDelay();

    public:
        // isDelayed parameter determines if a delay should be added to every vertex and edge
        // visit to make the graph algos run slower and get more reasonable runtimes. The user
        // sends this param from the frontend
        Vertex( size_t value, bool isDelayed ) : m_value( value ), m_isDelayed( isDelayed ), m_state( s_colour.white ) {}
        // copy cstor
        Vertex( const Vertex& v ) : m_value( v.m_value ) {
            std::cout << "Copy cstor" << std::endl;
            m_adjPtrList = v.m_adjPtrList;
        }
        // move cstor
        Vertex( const Vertex&& v ) : m_value( std::move( v.m_value ) ) {
            std::cout << "Move cstor" << std::endl;
            m_adjPtrList = std::move(v.m_adjPtrList);
        }
        // copy assignment
        // TODO: correct these functions or delete 'em
        Vertex& operator=( Vertex& v ) {
            std::cout << "Copy ass." << std::endl;
            *this = v;
            return *this;
        }
        // move assignment
        Vertex& operator=( Vertex&& v ) {
            std::cout << "Move ass." << std::endl;
            return v;
        }
        
        bool addNeighbour( Vertex* pV );

        const size_t getValue();

        int getState();
        
        void setState( int s );

        void resetNeighbours();

        VertexPtrList& getNeighbours();
    };

    class Graph
    {
    private:
        VertexList m_vertices;
        size_t m_numVertices;
        // set if a "more expensive" search should be used - see Vertex::setState()
        // for more details
        bool m_isDelayed;

    public:
        // no-params cstor
        Graph( bool isDelayed ) : m_numVertices( 0 ), m_isDelayed( isDelayed ) {}
        // copy cstor
        Graph( const Graph& graph ) {}
        // dstor
        ~Graph() {}
        // copy assignment
        // TODO: review
        Graph& operator=( const Graph& rhs )
        {
            m_numVertices = rhs.m_numVertices;
            m_vertices.reserve( m_numVertices );
            for ( auto v : rhs.m_vertices )
                m_vertices.emplace_back( v );
            // change pointers to point to new graph vertices
            for ( auto& v : m_vertices ) {
                for ( auto i = 0; i < v.getNeighbours().size(); ++i ) {
                    v.getNeighbours()[ i ] = &m_vertices[ v.getNeighbours()[ i ]->getValue() ];
                }
            }
            return *this;
        }

        // move assignment
        // TODO: review
        Graph& operator=( Graph&& rhs ) {
            m_vertices = std::move( rhs.m_vertices );
            m_numVertices = rhs.m_numVertices;
            return *this;
        }

        void addVertex( size_t key );

        VertexList& getVertices();
        
        // array operator
        Vertex& operator[]( size_t index );

        // reserves space of newSize in vector, sets size, and returns former size
        size_t setNumVertices( size_t newSize );
        
        size_t getNumVertices();

        void resetEdges();

        void printVertices();

        void printGraph();
    };
}

#endif
