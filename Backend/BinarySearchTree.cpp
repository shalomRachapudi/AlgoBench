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
BinarySearchTree::Node* BinarySearchTree::search( const int& x ) const
{
    return search( x, m_root );
}

BinarySearchTree::Node* BinarySearchTree::search( const int& x, BinarySearchTree::Node* t ) const
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
BinarySearchTree::Node* BinarySearchTree::minValue( Node *t ) const
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
BinarySearchTree::Node* BinarySearchTree::maxValue( Node *t ) const
{
    if ( t == NULL )
        return NULL;
    
    while ( t->m_right != NULL )
        t = t->m_right;
    
    return t;
}

/* size(): implementation */
int BinarySearchTree::size() const
{
    return m_treeSize;
}

/* insert(): implementation of wrapper and the actual method
 *
 * FIXME: Extend support for duplicates as well?
 */
void BinarySearchTree::insert( const int& x ) 
{
    insert( x, m_root );
    updateRange( x );
}
void BinarySearchTree::insert( const int& x, Node*& t )
{
    std::cout << "in here" << std::endl;
    if ( t == NULL ) {
        t = new Node( x, NULL, NULL );
        m_treeSize++; // update tree size
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
        BinarySearchTree::Node *oldNode = t;
        t = ( t->m_left != NULL ) ? t->m_left : t->m_right; // update root
        delete oldNode;
        
        m_treeSize--; // update tree size
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
        
        m_treeSize--; // update tree size
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
