/***********************************************************************************
 *  AlgoBench is a learning aid directed at students taking the Inf2B course to
 *  better understand the theoretical concepts, experiment with algorithms using 
 *  various inputs, and interpret the results.
 * 
 *  Copyright (C) 2017  Shalom <shalomray7@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  version 2 any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************************
 *  This program's developed as part of MSc project, University of Edinburgh (2017)
 *  Project: AlgoBench
 *  Supervisor: Kyriakos Kalorkoti
 *  School: School of Informatics
 *  Previous Contributor(s): None
 *  Aim: Demo BST program to calculate its runtime and space complexities based on
 *       the tokens it generates.
 ***********************************************************************************/

#include "BinarySearchTree.h"
using namespace inf2b;

/* search(): implementation of wrapper and the actual method */
inf2b::Node* BinarySearchTree::search( const int& x ) const
{
    return search( x, m_root );
}

inf2b::Node* BinarySearchTree::search( const int& x, inf2b::Node* t ) const
{
    while ( t != NULL ) {
        if ( x < t->m_data )
            t = t->m_left;
        else if ( t->m_data < x )
            t = t->m_right;
        else 
            return t; // node with value x found
    }
    return NULL;    // not found
}

/* min(): implementation of wrapper and the actual method */
int BinarySearchTree::min() const
{
    return minValue( m_root )->m_data;
}
inf2b::Node* BinarySearchTree::minValue( Node *t ) const
{
    if ( t == NULL )
        return NULL;
    
    while ( t->m_left != NULL )
        t = t->m_left;
    
    return t;
}

/* max(): implementation of wrapper and the actual method */
int BinarySearchTree::max() const
{
    return maxValue( m_root )->m_data;
}
inf2b::Node* BinarySearchTree::maxValue( Node *t ) const
{
    if ( t == NULL )
        return NULL;
    
    while ( t->m_right != NULL )
        t = t->m_right;
    
    return t;
}

/* size(): implementation */
int BinarySearchTree::size()
{
    return m_treeSize;
}

/* insert(): implementation of wrapper and the actual method
 *
 * FIXME: Extend support for duplicates as well?
 */
void BinarySearchTree::insert( const InputIntType& x ) 
{
    insert( x, m_root );
    updateRange( x );
}
void BinarySearchTree::insert( const InputIntType& x, Node*& t )
{
    if ( t == NULL ) {
        t = new Node( x, NULL, NULL );
    }
    else if ( x < t->m_data )
        insert( x, t->m_left );
    else if ( t->m_data < x )
        insert( x, t->m_right );
}

/* remove(), and removeMin(): implementation of wrapper and the actual method */
void BinarySearchTree::remove( const int& x ) 
{
    remove( x, m_root );
    updateRange();
}
void BinarySearchTree::remove( const int& x, Node*& t )
{
    if ( t == NULL )
        return;
    
    if ( x < t->m_data )
        remove( x, t->m_left );
    else if ( t->m_data < x )
        remove( x, t->m_right );
    else if ( t->m_left != NULL && t->m_right != NULL )
    {
        t->m_data = minValue( t->m_right )->m_data;
        removeMin( t->m_right ); // remove min of right subtree
    }
    else
    {
        inf2b::Node *oldNode = t;
        t = ( t->m_left != NULL ) ? t->m_left : t->m_right; // update root
        delete oldNode;
    }
}

void BinarySearchTree::removeMin( Node*& t )
{
    if ( t == NULL )
        return;
    else if ( t->m_left != NULL )
        removeMin( t->m_left );
    else {
        Node *temp = t;
        t = t->m_right;
        delete temp;
    }
}

/* updateRange() and range() implementation */
void BinarySearchTree::updateRange( const int& x )
{
    if ( x < m_lowerBound )
        m_lowerBound = x;
    if ( x > m_upperBound )
        m_upperBound = x;
}
void BinarySearchTree::updateRange()
{
    m_lowerBound = min();
    m_upperBound = max();
}
std::string BinarySearchTree::range()
{
    std::stringstream ss;
    ss << '[' << m_lowerBound << ", " << m_upperBound << ']';
    return ss.str();    // returns string of format [23, 90]
}

/* height(): implementation of wrapper and the actual method */
int BinarySearchTree::height()
{
    return height( m_root );
}
int BinarySearchTree::height( Node* t )
{
    if ( t == NULL )
        return -1;
    
    int leftHeight = height( t->m_left );
    int rightHeight = height( t->m_right );
    
    return ( leftHeight > rightHeight ) ? ( leftHeight + 1 )
                                        : ( rightHeight + 1 );
}

/* nodeLevel() : implementation */
int inf2b::BinarySearchTree::getNodeLevel( const InputIntType& key )
{
    return getNodeLevel( m_root, key, 0 );
}

int inf2b::BinarySearchTree::getNodeLevel( Node* t, int data, int level )
{
    if ( t == NULL )
        return 0;
    if ( t->m_data == data )
        return level;
    
    int nodeLevel = getNodeLevel( t->m_left, data, level + 1 );
    if ( nodeLevel != 0 )
        return nodeLevel;
    nodeLevel = getNodeLevel( t->m_right, data, level + 1);
    return nodeLevel;
}

size_t inf2b::nodeSize()
{
    return sizeof( Node );
}

size_t inf2b::nodeSizeWithRef()
{
    // create a temporary node
    Node *temp = new Node(0, NULL, NULL);
    
    size_t sizeWithRef =  sizeof( temp ) + nodeSize();
    
    // delete temp
    delete temp;
    return sizeWithRef;
}

void BinarySearchTree::operator()()
{
    std::cout << "Inserting keys into BST" << std::endl;

   	/* report progress */
   	int tenPercentLimit = m_treeSize / 100;
   	int totalProgress = 0;
   	int nodesSoFar = 0;
    for (InputIntType element : m_input) {
        insert( element );

        nodesSoFar++;
        if (nodesSoFar == tenPercentLimit) {
        	totalProgress += 1;
        	nodesSoFar = 0;

        	std::cout << "[TOTALPROGRESS]\t" << totalProgress << std::endl;
        }
    }
    
    std::cout << "[TASKRUNNER] Finished Inserting keys into BST. Size = " << size( m_root ) << std::endl;
    
    // send a msg to update tree contruction time
    std::cout << "[UPDATETIME]\tnow" << std::endl;
    
    // update height of the tree
    std::cout << "[TREEHEIGHT]\t" << height() << std::endl;
    
    
    // Start basic operations
    std::chrono::time_point< std::chrono::high_resolution_clock > start_time, end_time;
    
    // insertion
    if ( m_insertOp ) {
        start_time = std::chrono::high_resolution_clock::now();
        insert( m_dataElement );
        end_time = std::chrono::high_resolution_clock::now();
        
        // report time
        std::cout << "[INSERTOPTIME]\t"
                            << std::chrono::duration_cast<std::chrono::nanoseconds>(end_time - start_time).count() << std::endl;
    }
    
    // report the node level
    std::cout << "[NODELEVEL]\t" << getNodeLevel( m_dataElement ) << std::endl;
    
    //search
    if ( m_searchOp ) {
        start_time = std::chrono::high_resolution_clock::now();
        search( m_dataElement );
        end_time = std::chrono::high_resolution_clock::now();
        
        // report time
        std::cout << "[SEARCHOPTIME]\t"
                            << std::chrono::duration_cast<std::chrono::nanoseconds>(end_time - start_time).count() << std::endl;
    
    }
    
    // delete
    if ( m_deleteOp ) {
        start_time = std::chrono::high_resolution_clock::now();
        remove( m_dataElement );
        end_time = std::chrono::high_resolution_clock::now();
        
        // report time
        std::cout << "[DELETEOPTIME]\t"
                            << std::chrono::duration_cast<std::chrono::nanoseconds>(end_time - start_time).count() << std::endl;
    
    }
}

/*********************** Utility methods **********************/
void BinarySearchTree::print() const
{
    inorder( m_root );
}

void BinarySearchTree::inorder( Node* t ) const
{
    if ( t == NULL )
        return;
    inorder( t->m_left );
    std::cout << t->m_data << " ";
    inorder( t->m_right );
}


bool BinarySearchTree::isEmpty() const
{
    return m_root == NULL;
}


int inf2b::BinarySearchTree::size( Node* t )
{
    if ( t == NULL )
        return 0;
    return ( size( t->m_left ) + 1 + size( t->m_right ) );
}

