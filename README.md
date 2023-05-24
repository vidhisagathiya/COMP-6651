# COMP-6651
K-Colorable Graph

Graph coloring is a fundamental problem in computer science and discrete math- ematics, with applications spanning a wide range of areas, 
including scheduling, frequency assignment, register allocation, and more. In graph coloring, the ob- jective is to assign colors to the vertices 
of a graph in such a way that no two adjacent vertices share the same color. The minimum number of colors required to color a graph is called the chromatic number. 
Finding the chromatic num- ber is an NP-hard problem, which has motivated the development of various heuristics and approximation algorithms for graph coloring. 
One such approxi- mation algorithm is the F irst − F it algorithm, a greedy coloring algorithm that assigns the smallest available color to each vertex in a sequential order. 
The FirstFit algorithm is simple and has a low computational complexity, making it a popular choice for many applications. 
However, its performance can vary significantly depending on the structure and properties of the input graph. 

In this project, we focus on studying the average competitive ratio of the FirstFit algorithm on triangle-free graphs. 
Triangle-free graphs are graphs that do not contain any cycles of length three, meaning they have no triangles. 
These graphs are of interest because they exhibit certain properties that make them suitable for various applications and can provide insights into the behavior of coloring algorithms.

The objectives of this project are to:
• Develop an approach to generate triangle-free graphs of n vertices by cre- ating random graphs using the Erdo ̋s − Re ́nyi model and iteratively re- moving triangles.
• Apply the FirstFit algorithm to the generated triangle-free graphs and compute the average number of colors used.
• Analyze the dependency of the average number of colors used by the FirstFit algorithm on the number of vertices n.
