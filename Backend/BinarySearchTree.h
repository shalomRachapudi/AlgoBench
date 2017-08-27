#ifndef BINARYSEARCHTREE_H
#define BINARYSEARCHTREE_H


#pragma once
#include "stdafx.h"
#include "global.h"

namespace inf2b 
{
    class Node 
    {
    public:
        int m_data;
        Node *m_left, *m_right; // links for left and right subtrees
        
        Node( const int& data, Node *left, Node *right )
                : m_data ( data ),
                m_left ( left ),
                m_right ( right ) {}
    };
    
    /** 
     * Get (memory) size of Node class
     */
    size_t nodeSize();
    
    /**
     * Get (memory) size of Node class with head reference
     */
    size_t nodeSizeWithRef();
    
    class BinarySearchTree 
    {
    private:
                
        Node *m_root;
        int m_treeSize;
        int m_lowerBound;
        int m_upperBound;
        bool m_insertOp;
        bool m_searchOp;
        bool m_deleteOp;
        std::string m_output;
        InputIntType m_dataElement;
        InputVectorType& m_input;
        
        /**
         * Search for an element in BST.
         * 
         * Returns node* on success or NULL on failure.
         */
        Node* search( const int& x, Node* t) const;
        
        /**
         * Returns Node of minimum element of the BST
         */
        Node* minValue( Node* t ) const;
        
        /**
         * Returns maximum element of the BST
         */
        Node* maxValue( Node* t) const;
        
        /**
         * Inserts an integer value x into the BST.
         */
        void insert( const InputIntType& x, Node*& t );
        
        /**
         * Removes the value x from the BST.
         */
        void remove( const int& x, Node*& t );
        
        /**
         * Removes minimum element from this BST
         */
        void removeMin( Node*& t );
        
        /**
         * Prints inorder of BST 
         */
        void inorder( Node* t ) const;
        
        /**
         * Updates range given an integer
         */
        void updateRange( const int& x );
        
        /**
         * Updates range using min() and max() methods
         */
        void updateRange();
        
        /**
         * Calculates height of this BST
         */
        int height( Node *t );
        
        /**
         * Calculates size of this BST
         */
        int size( Node *t );
        
        /**
         * Returns level of the node if key is present in tree
         * 
         * Otherwise returns -1
         */
        int getNodeLevel( Node * t, int data, int level );
        
    public:
        BinarySearchTree( InputVectorType& input,
                          size_t treeSize,
                          size_t min,
                          size_t max,
                          InputIntType dataElement,
                          bool insertOp,
                          bool searchOp,
                          bool deleteOp )  :
                          m_root ( NULL ),
                          m_treeSize ( treeSize ),
                          m_lowerBound ( min ),
                          m_upperBound ( max ),
                          m_insertOp ( insertOp ),
                          m_searchOp ( searchOp ),
                          m_deleteOp ( deleteOp ),
                          m_dataElement ( dataElement ),
                          m_input ( input )
                        {}
                        
        /**
         * Returns m_output : m_output is sent to front-end to create runtime chart / table
         */
        std::string operator()();
        
        /**
         * Returns minimum element of the BST
         * Wrapper for minValue() method.
         */
        int min() const;
        
        /**
         * Returns maximum element of the BST
         * Wrapper for maxValue() method.
         */
        int max() const;
        
        /**
         * Search for an element in BST.
         * Return 1 on success or -1 on failure.
         * 
         * Wrapper for search( int&, Node* ) method
         */
        Node* search( const int& x ) const;
        
        /**
         * Returns true if this BST is empty
         */
        bool isEmpty() const;
        
        /**
         * Inserts an integer value x into the BST.
         * 
         * Wrapper for insert( int&, Node* ) method
         */
        void insert( const InputIntType& x );
        
        
        /**
         * Removes the integer value x from the BST.
         * 
         * Wrapper for remove( int&, Node* ) method
         */
        void remove( const int& x);
        
        /**
         * Returns maximum recursion depth of this BST
         * 
         * FIXME: Implement this
         */
        int maximumRecursionDepth();
        
        /**
         *  Returns height of this BST (a 1-node tree has height 0)
         * 
         *  -1 if no node exists and height h on success
         */
        int height();
        
        /**
         * Returns the range of values BST holds
         * 
         * Return type: std::string; Eg: [minValue(), maxValue()]
         */
        std::string range();
        
        /**
         * Print BST
         */
        void print() const;
        
        /**
         * Returns the size of the tree 
         */
        int size();
        
        /**
         * Returns level of node
         */
        int getNodeLevel( const InputIntType& key );
        
        /**
         * Returns progress
         */
        std::string progress();
        
        
    };
}
#endif
