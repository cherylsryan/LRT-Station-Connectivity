/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.assg;

import java.util.List;

public interface Graph<V> {
    public int getSize();
    public List<V> getVertices();
    public V getVertex(int index);
    public int getIndex(V v);
    public List<Integer> getNeighbors(int index);
    public int getDegree(int index);
    public void printEdges();
    public void clear();
    public boolean addVertex(V v);
    public boolean addEdge(int u, int v);
  
    public AbstractGraph<V>.Tree dfs(int v);
    public AbstractGraph<V>.Tree bfs(int v);
}
