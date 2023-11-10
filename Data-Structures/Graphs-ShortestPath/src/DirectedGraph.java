package GraphPackage;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ADTPackage.*; // Classes that implement various ADTs

/**
 A class that implements the ADT directed graph.
 @author Frank M. Carrano
 @author Timothy M. Henry
 @version 5.1
 */
public class DirectedGraph<T> implements GraphInterface<T>
{

   private DictionaryInterface<T, VertexInterface<T>> vertices;
   private int edgeCount;
   
   public DirectedGraph()
   {

      vertices = new UnsortedLinkedDictionary<>();
      edgeCount = 0;
   } // end default constructor

   public boolean addVertex(T vertexLabel)
   {
      VertexInterface<T> addOutcome = vertices.add(vertexLabel, new Vertex<>(vertexLabel));
      return addOutcome == null; // Was addition to dictionary successful?
   } // end addVertex
   
   public boolean addEdge(T begin, T end, double edgeWeight)
   {
      boolean result = false;
      VertexInterface<T> beginVertex = vertices.getValue(begin);
      VertexInterface<T> endVertex = vertices.getValue(end);
      if ( (beginVertex != null) && (endVertex != null) )
         result = beginVertex.connect(endVertex, edgeWeight);
      if (result)
         edgeCount++;
      return result;
   } // end addEdge
   
   public boolean addEdge(T begin, T end)
   {
      return addEdge(begin, end, 0);
   } // end addEdge

   public boolean hasEdge(T begin, T end)
   {
      boolean found = false;
      VertexInterface<T> beginVertex = vertices.getValue(begin);
      VertexInterface<T> endVertex = vertices.getValue(end);
      if ( (beginVertex != null) && (endVertex != null) )
      {
         Iterator<VertexInterface<T>> neighbors = beginVertex.getNeighborIterator();
         while (!found && neighbors.hasNext())
         {
            VertexInterface<T> nextNeighbor = neighbors.next();
            if (endVertex.equals(nextNeighbor))
               found = true;
         } // end while
      } // end if
      
      return found;
   } // end hasEdge

	public boolean isEmpty()
	{
	  return vertices.isEmpty();
	} // end isEmpty

	public void clear()
	{
	  vertices.clear();
	  edgeCount = 0;
	} // end clear

	public int getNumberOfVertices()
	{
	  return vertices.getSize();
	} // end getNumberOfVertices

	public int getNumberOfEdges()
	{
	  return edgeCount;
	} // end getNumberOfEdges

	protected void resetVertices()
	{
	   Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
	   while (vertexIterator.hasNext())
	   {
	      VertexInterface<T> nextVertex = vertexIterator.next();
	      nextVertex.unvisit();
	      nextVertex.setCost(0);
	      nextVertex.setPredecessor(null);
	   } // end while
	} // end resetVertices

	@Override
	public QueueInterface<T> getDepthFirstSearch(T origin, T end) {
	   resetVertices();
		QueueInterface<T> traversalOrder = new LinkedQueue<>();
		StackInterface<VertexInterface<T>> vertexStack = new LinkedStack<>();

		VertexInterface<T> originVertex = vertices.getValue(origin);
		originVertex.visit();

		traversalOrder.enqueue(origin);
		vertexStack.push(originVertex);

		while (!vertexStack.isEmpty()) {
			VertexInterface<T> topVertex =vertexStack.peek();

			if(topVertex.getUnvisitedNeighbor() != null){
				VertexInterface<T> nextNeighbor =topVertex.getUnvisitedNeighbor();
				nextNeighbor.visit();

				traversalOrder.enqueue(nextNeighbor.getLabel());
				vertexStack.push(nextNeighbor);

				if(nextNeighbor.getLabel().equals(end)){
					return traversalOrder;
				}
			}else {
				vertexStack.pop();
			}


		}

		return traversalOrder;
	}
	public StackInterface<T> getTopologicalOrder()
	{
		resetVertices();

		StackInterface<T> vertexStack = new LinkedStack<>();
		int numberOfVertices = getNumberOfVertices();
		for (int counter = 1; counter <= numberOfVertices; counter++)
		{
			VertexInterface<T> nextVertex = findTerminal();
			nextVertex.visit();
			vertexStack.push(nextVertex.getLabel());
		} // end for
		
		return vertexStack;	
	} // end getTopologicalOrder

	@Override
	public int getShortestPath(T begin, T end, StackInterface<T> path) {
	   resetVertices();
	   boolean done = false;
	   QueueInterface<VertexInterface<T>> vertexQueue = new LinkedQueue<>();
	   VertexInterface<T> originVertex = vertices.getValue(begin);
		VertexInterface<T> endVertex = vertices.getValue(end);

	   originVertex.visit();

	   vertexQueue.enqueue(originVertex);

	   while (!done && !vertexQueue.isEmpty()) {
		   VertexInterface<T> frontVertex = vertexQueue.dequeue();
		   Iterator<VertexInterface<T>> neighbors = frontVertex.getNeighborIterator();

		   while (!done && neighbors.hasNext()) {
			   VertexInterface<T> nextNeighbor = neighbors.next();

			   if(!nextNeighbor.isVisited()) {
				   nextNeighbor.visit();
				   nextNeighbor.setCost(1+frontVertex.getCost());
				   nextNeighbor.setPredecessor(frontVertex);
				   vertexQueue.enqueue(nextNeighbor);
			   }
			   if(nextNeighbor.equals(endVertex))
				   done = true;

		   }
	   }

	   int pathLength = (int) endVertex.getCost();
	   path.push(endVertex.getLabel());

	   VertexInterface<T> vertex = endVertex;
	   while (vertex.hasPredecessor()) {
		   vertex = vertex.getPredecessor();
		   path.push(vertex.getLabel());
	   }
		return pathLength;

	}

	@Override
	public double getCheapestPath(T begin, T end, StackInterface<T> path) {
	   if(path ==null){
		   System.out.println("Path should be empty stack, not null!");
		   throw new NullPointerException();
	   }

		resetVertices();
		boolean done = false;
		double pathCost = 0;

		PriorityQueueInterface<EntryPQ> priorityQueue = new HeapPriorityQueue<>();
		priorityQueue.add(new EntryPQ(vertices.getValue(begin),0,null));
		VertexInterface<T> endVertex = vertices.getValue(end);

		while (!done && !priorityQueue.isEmpty()) {
			EntryPQ frontEntry = priorityQueue.remove();
			VertexInterface<T> frontVertex = frontEntry.getVertex();

			if(!frontVertex.isVisited()) {
				frontVertex.visit();
				frontVertex.setCost(frontEntry.getCost());
				frontVertex.setPredecessor(frontEntry.getPredecessor());

				if(frontVertex.getLabel().equals(end)) {
					done = true;
				}else {
					Iterator<VertexInterface<T> > neigborsOfFront = frontVertex.getNeighborIterator();
					Iterator<Double> weightIteOfFrontV = frontVertex.getWeightIterator();

					while (neigborsOfFront.hasNext()) {
						VertexInterface<T> nextNeighbor = neigborsOfFront.next();
						double weightOfFront = weightIteOfFrontV.next();

						if(!nextNeighbor.isVisited()) {
							double nextCost =weightOfFront+pathCost;
							priorityQueue.add(new EntryPQ(nextNeighbor, nextCost,frontVertex));

							pathCost=nextCost;
						}

					}

				}

			}

		}

		path.push(end);
		VertexInterface<T> traversalV = endVertex;

		while(traversalV.hasPredecessor()){
			traversalV = traversalV.getPredecessor();
			path.push(traversalV.getLabel());
		}

		return pathCost;
	}

	@Override
	public QueueInterface<T> getBreadthFirstSearch(T origin, T end) {
	   resetVertices();
		QueueInterface<T> traversalOrder = new LinkedQueue<>();
		QueueInterface<VertexInterface<T>> vertexQueue = new LinkedQueue<>();

		VertexInterface<T> originVertex = vertices.getValue(origin);

		originVertex.visit();
		traversalOrder.enqueue(origin);
		vertexQueue.enqueue(originVertex);

		while (!vertexQueue.isEmpty()) {

			VertexInterface<T> frontVertex = vertexQueue.dequeue();
			Iterator<VertexInterface<T>> front_neighborIte = frontVertex.getNeighborIterator();

			while (front_neighborIte.hasNext()) {
				VertexInterface<T> nextNeighbor = front_neighborIte.next();


				if(!nextNeighbor.isVisited()) {
					nextNeighbor.visit();
					traversalOrder.enqueue(nextNeighbor.getLabel());
					vertexQueue.enqueue(nextNeighbor);
				}

				if(nextNeighbor.getLabel().equals(end)){
					return traversalOrder;
				}
			}
		}
		return traversalOrder;

	}
  
	public void printAdjacencyMatrix() {

	   Iterator<VertexInterface<T>> verIterator =vertices.getValueIterator();
	   String[] labels = new String[vertices.getSize()];

	   int count = labels.length-1;
	   while (verIterator.hasNext()) {
		   VertexInterface<T> vertex = verIterator.next();
		   labels[count] =(String) vertex.getLabel();
		   count--;
	   }

		System.out.print(String.format("   %6s",""));
	   for(String label : labels) {
		   System.out.print(String.format(" %6s",label));
	   }
		System.out.println();

	   for(String label : labels) {
		   System.out.print(String.format(" %6s",label));

		   for(String label2 : labels) {

			   if(hasEdge((T)label,(T) label2)) {
				   System.out.print(String.format("  %5d",1));
			   }else {
				   System.out.print(String.format("  %5d",0));
			   }

		   }

		   System.out.println();
	   }

	}


   //###########################################################################
   /*   public QueueInterface<T> getDepthFirstSearch(T origin, T end) 
    * 		return depth first search traversal order between origin vertex and end vertex
    */
   //###########################################################################
		
	
	
	
	//###########################################################################
	   /*   public int getShortestPath(T begin, T end, StackInterface<T> path) 
	    * 		return the shortest path between begin vertex and end vertex
	    */
    //###########################################################################
  
   
	
   
    //###########################################################################
	/** Precondition: path is an empty stack (NOT null) */
    /* Use EntryPQ instead of Vertex in Priority Queue because multiple entries contain
     * 	the same vertex but different costs - cost of path to vertex is EntryPQ's priority value
     * public double getCheapestPath(T begin, T end, StackInterface<T> path)
     * 		return the cost of the cheapest path
     */
    //###########################################################################


	
	protected VertexInterface<T> findTerminal()
	{
		boolean found = false;
		VertexInterface<T> result = null;

		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();

		while (!found && vertexIterator.hasNext())
		{
			VertexInterface<T> nextVertex = vertexIterator.next();
			
			// If nextVertex is unvisited AND has only visited neighbors)
			if (!nextVertex.isVisited())
			{ 
				if (nextVertex.getUnvisitedNeighbor() == null )
				{ 
					found = true;
					result = nextVertex;
				} // end if
			} // end if
		} // end while

		return result;
	} // end findTerminal

	// Used for testing
	public void displayEdges()
	{
		System.out.println("\nEdges exist from the first vertex in each line to the other vertices in the line.");
		System.out.println("(Edge weights are given; weights are zero for unweighted graphs):\n");
		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
		while (vertexIterator.hasNext())
		{
			((Vertex<T>)(vertexIterator.next())).display();
		} // end while
	} // end displayEdges 

	public void connectEdges() {
		//connecting edges by using contains method.
		Iterator<T> keyIterator = vertices.getKeyIterator();

		while (keyIterator.hasNext()) {
			String labelOfVertex = (String) keyIterator.next();

			String[] coordinates = labelOfVertex.split("-");
			int rowNum = Integer.parseInt(coordinates[0]);
			int colNum = Integer.parseInt(coordinates[1]);

			try {
				if(vertices.contains( (T) String.format("%d-%d",(rowNum-1),colNum))) {
					addEdge((T) String.format("%d-%d",(rowNum-1),colNum),(T) String.format("%d-%d",(rowNum),colNum) ,  (int) (4*Math.random() + 1) );
				}
				if(vertices.contains( (T) String.format("%d-%d",(rowNum+1),colNum))) {
					addEdge((T) String.format("%d-%d",(rowNum+1),colNum),(T) String.format("%d-%d",(rowNum),colNum),  (int) (4*Math.random() + 1) );
				}
				if(vertices.contains( (T) String.format("%d-%d",(rowNum),(colNum-1) ) )  ) {
					addEdge((T) String.format("%d-%d",rowNum,(colNum-1)),(T) String.format("%d-%d",rowNum,colNum),  (int) (4*Math.random() + 1) );
				}
				if(vertices.contains( (T) String.format("%d-%d",(rowNum),(colNum+1) ) )  ) {
					addEdge((T) String.format("%d-%d",rowNum,(colNum+1)),(T) String.format("%d-%d",rowNum,colNum),  (int) (4*Math.random() + 1));
				}


			}catch (Exception e) {
				System.out.println("an error occured while casting string to a generic");
				e.printStackTrace();
			}



		}
	}

	private class EntryPQ implements Comparable<EntryPQ>
	{
		private VertexInterface<T> vertex; 	
		private VertexInterface<T> previousVertex; 
		private double cost; // cost to nextVertex
		
		private EntryPQ(VertexInterface<T> vertex, double cost, VertexInterface<T> previousVertex)
		{
			this.vertex = vertex;
			this.previousVertex = previousVertex;
			this.cost = cost;
		} // end constructor
		
		public VertexInterface<T> getVertex()
		{
			return vertex;
		} // end getVertex
		
		public VertexInterface<T> getPredecessor()
		{
			return previousVertex;
		} // end getPredecessor

		public double getCost()
		{
			return cost;
		} // end getCost
		
		public int compareTo(EntryPQ otherEntry)
		{
			// Using opposite of reality since our priority queue uses a maxHeap;
			// could revise using a minheap
			return (int)Math.signum(otherEntry.cost - cost);
		} // end compareTo
		
		public String toString()
		{
			return vertex.toString() + " " + cost;
		} // end toString 
	} // end EntryPQ
} // end DirectedGraph
