/*
 * Copyright (C) 2011 Cozycode.net
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cozycode.swinginspector.components;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.cozycode.swinginspector.IInspectionListener;

//TODO: When a node is collapsed, prune off it's children's children
//TODO: Add listeners to each container node to watch for added/removed nodes and update accordingly
public class ComponentTree extends JTree {
   private static final long serialVersionUID = 1L;

   private final CopyOnWriteArrayList<IInspectionListener> inspectionListeners = new CopyOnWriteArrayList<IInspectionListener>();

   public ComponentTree() {
      this( null );
   }

   public ComponentTree( Component inspecting ) {
      this.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
      this.setEditable( false );

      inspectComponent( inspecting );

      addTreeSelectionListener( new TreeSelectionListener() {
         public void valueChanged( TreeSelectionEvent event ) {
            Object obj = event.getPath().getLastPathComponent();
            if( obj instanceof ComponentNode ) {
               ComponentNode node = (ComponentNode)obj;

               fireInspectionChanged( node.getComponent() );
            }
         }
      });

      addTreeWillExpandListener( new TreeWillExpandListener() {
         public void treeWillExpand( TreeExpansionEvent event ) throws ExpandVetoException {
            Object obj = event.getPath().getLastPathComponent();
            if( obj instanceof ComponentNode ) {
               ComponentNode node = (ComponentNode)obj;

               for( int i = 0; i < node.getChildCount(); ++i ) {
                  TreeNode child = node.getChildAt( i );
                  if( child instanceof ComponentNode ) {
                     addChildrenToNode( (ComponentNode)child );  
                  }
               }
            }
         }

         public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            /** noop */
         }
      });  
   }



   public void inspectComponent( Component selection ) {
      if( selection == null ) {
         setModel( new DefaultTreeModel( null ));
         fireInspectionChanged( null );
         return;
      }
      ArrayDeque<TreeNode> pathBuilder = new ArrayDeque<TreeNode>();
      ComponentNode rootNode = new ComponentNode( selection );
      addChildrenToNode( rootNode );
      pathBuilder.push( rootNode );

      Component iter = selection.getParent();

      while( iter != null ) {
         ComponentNode parentNode = new ComponentNode( iter );
         addChildrenToNode( parentNode, rootNode );
         addChildrenToChildren( parentNode );
         pathBuilder.push( parentNode );

         rootNode = parentNode;
         iter = iter.getParent();
      }

      setModel( new DefaultTreeModel( rootNode ));
      setSelectionPath( new TreePath( pathBuilder.toArray() ));

      fireInspectionChanged( selection );
   }

   /**
    * Adds the appropriate children to the children of this node by
    * calling 'addChildrenToNode' for each child.
    */
   private static void addChildrenToChildren( ComponentNode node ) {
      int length = node.getChildCount();
      for( int i = 0; i < length; ++i ) {
         if( node.getChildAt( i ) instanceof ComponentNode ) {
            addChildrenToNode( (ComponentNode) node.getChildAt( i ));
         }
      }
   }


   /**
    * If the specified node has no children, then ComponentNodes 
    * are created and added for each child component of the node's 
    * underlying component.
    */
   private static void addChildrenToNode( ComponentNode node ) {
      addChildrenToNode( node, null );
   }

   /**
    * If the specified node has no children, then ComponentNodes 
    * are created and added for each child component of the node's 
    * underlying component.
    * 
    * When a child component which matches the component of 'knownChild'
    * is encountered, the knownChild is added instead of creating a new
    * node for it.
    */
   private static void addChildrenToNode( ComponentNode node, ComponentNode knownChild ) {
      if( node.getChildCount() == 0 && node.isContainer()) {
         for( Component child : node.getContainer().getComponents() ) {
            if( knownChild == null || child != knownChild.getComponent() ) {
               node.add( new ComponentNode( child ));
            }
            else {
               node.add( knownChild );
            }
         }
      }
   }

   protected void fireInspectionChanged( Component comp ) {
      for( IInspectionListener listener : inspectionListeners ) {
         listener.inspectionChanged( comp );
      }
   }

   public void addInspectionListener( IInspectionListener listener ) {
      inspectionListeners.add( listener );
   }

   public void removeInspectionListener( IInspectionListener listener ) {
      inspectionListeners.remove( listener );
   }

   @Override
   public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      if( value instanceof ComponentNode ) {
         Component component = ((ComponentNode)value).getComponent();

         if( component == null ) { return "null"; }

         return component.getClass().getSimpleName();
      }
      else {
         return "ERROR: TreeNode was not a ComponentNode as was expected";
      }
   }

   protected static class ComponentNode extends DefaultMutableTreeNode {
      private static final long serialVersionUID = 1L;

      public ComponentNode( Component component ) {
         super( component );
      }

      public Component getComponent() {
         return (Component)getUserObject();
      }

      public boolean isContainer() {
         return getUserObject() instanceof Container;
      }

      public Container getContainer() {
         return isContainer() ? (Container)getUserObject() : null;
      }
   }
}
