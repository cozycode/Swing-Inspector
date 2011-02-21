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

package net.cozycode.swinginspector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;

import net.cozycode.constructs.IClosure;
import net.cozycode.swinginspector.decorators.BackgroundDecorator;
import net.cozycode.swinginspector.decorators.BorderDecorator;
import net.cozycode.swinginspector.decorators.IComponentDecorator;
import net.cozycode.swinginspector.decorators.NullDecorator;
import net.cozycode.swinginspector.inspectors.ComponentInspector;
import net.cozycode.swinginspector.inspectors.ContainerInspector;
import net.cozycode.swinginspector.inspectors.JComponentInspector;
import net.cozycode.swinginspector.inspectors.ObjectInspector;


//TODO: view registered listeners
//TODO: event logger
//TODO: event spoofer
//TODO: Object Inspector
//TODO: Scripting via on the fly compiling (for adding listeners)
//TODO: Make the panels collapsable
//TODO: Invoke specific methods: 
     /* revalidate
      * repaint
      */
//TODO: Inspect UI Properties


//TODO: Provide a ComboBox for selecting IComponentDecorators
//TODO: Use a weak reference or add a hierarchy listener to tell when it's no longer showing and then use this to cleanup() when it happens.
public class SwingInspectorController {
   private static final InspectorFactory factory = new InspectorFactory();
   private static final IComponentDecorator[] decorators = new IComponentDecorator[] {
      new NullDecorator(),
      new BorderDecorator( Color.BLUE, 2 ),
      new BackgroundDecorator( Color.CYAN )
   };

   private final MyWindowListener windowListener = new MyWindowListener();
   private final MyInspectionListener inspectionListener = new MyInspectionListener();
   private final DecoratorChangedListener decoratorListener = new DecoratorChangedListener();

   private SwingInspectorFrame frame = null;
   private IComponentDecorator decorator = decorators[1];
   private Component component = null;

   public SwingInspectorController() {
      factory.add( Object.class, ObjectInspector.class );
      factory.add( Component.class, ComponentInspector.class );
      factory.add( Container.class, ContainerInspector.class );
      factory.add( JComponent.class, JComponentInspector.class );
   }

   public void inspectComponent( Component c ) {
      if( component == c ) {
         moveDecoration( component, null );
         component = null;
         frameInspect( null );
         return;
      }

      moveDecoration( component, c );
      component = c;


      if( frame == null ) {
         frame = createFrame( component );
         frame.addInspectionListener( inspectionListener );
         frame.addDecoratorChangedListener( decoratorListener );
      }
      else {
         frameInspect( component );
      }
   }

   protected void moveDecoration( Component prev, Component next ) {
      try {
         if( prev != null && decorator != null ) { decorator.undecorate( prev ); }
         if( next != null && decorator != null ) { decorator.decorate( next ); }
      }
      catch( RuntimeException e ) {
         // Some swing operations aren't supported across all components and may throw unexpected exceptions.
         // We don't want those exceptions to kill the application, so we silently swallow them.
         // EX: JViewPort throws IllegalArgumentException when you attempt to change the border.
      }
   }

   protected void frameInspect( Component comp ) {
      frame.removeInspectionListener( inspectionListener );
      frame.inspectComponent( comp );
      frame.addInspectionListener( inspectionListener );
   }

   public void cleanup() {
      if( component != null ) { decorator.undecorate( component ); }
      component = null;

      frame.removeDecoratorChangedListener( decoratorListener );
      frame.removeInspectionListener( inspectionListener );
      frame.removeWindowListener( windowListener );
      frame.dispose();
      frame = null;
   }

   private SwingInspectorFrame createFrame( Component component ) {
      SwingInspectorFrame f = new SwingInspectorFrame( component, factory, decorators );
      f.setDecorator( decorators[1] );

      f.addWindowListener( windowListener );
      f.pack();
      f.setVisible( true );
      return f;
   }

   private final class MyWindowListener extends WindowAdapter {
      public void windowClosing(WindowEvent e) {
         cleanup();
      }
   }

   private final class MyInspectionListener implements IInspectionListener {
      public void inspectionChanged(Component c) {
         moveDecoration( component, c );
         component = c;
      }
   }

   private final class DecoratorChangedListener implements IClosure<IComponentDecorator> {
      @Override
      public void yield(IComponentDecorator item) {
         try {
            if( component != null && decorator != null ) {
               decorator.undecorate( component );
            }
            decorator = item;
            if( component != null && decorator != null ) {
               decorator.decorate( component );
            }
         }
         catch( RuntimeException e ) {
            // Some swing operations aren't supported across all components and may throw unexpected exceptions.
            // We don't want those exceptions to kill the application, so we silently swallow them.
            // EX: JViewPort throws IllegalArgumentException when you attempt to change the border.
         }
      }
   }
}
