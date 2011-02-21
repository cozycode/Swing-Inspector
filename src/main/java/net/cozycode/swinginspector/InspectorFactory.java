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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import net.cozycode.core.Reflecting;


public class InspectorFactory {
   private final Map<Class<?>, List<Class<? extends JComponent>>> allInspectors
      = new HashMap<Class<?>, List<Class<? extends JComponent>>>();


   /**
    * Adds the specified inspector to the set of inspectors for 'type'
    * 
    * @param type - the type of class this inspector works with.
    * @param inspector - the inspector
    */
   public void add( Class<?> type, Class<? extends JComponent> inspector ) {
      List<Class<? extends JComponent>> list = allInspectors.get( type );
      if( list == null ) {
         list = new ArrayList<Class<? extends JComponent>>();
         allInspectors.put( type, list );
      }
      list.add( inspector );
   }

   /**
    * Creates a list of all inspectors which match the specified type.
    * The list is returned in the order the inspectors are to be displayed.
    */
   public <T> JComponent[] createInspectors( T arg ) {
      // Use a stack so we see panels arranged from more general to more specific
      Deque<JComponent> stack = new ArrayDeque<JComponent>();

      Class<?> iter = arg.getClass();
      while( iter != null ) {
         JComponent[] all = createInspectors( iter, arg );

         //Add created items to the stack in reverse order
         //so that their relative ordering is maintained.
         //This allows us to add default panels first
         //and then append any user supplied plugins after.
         for( int i = all.length - 1; i >= 0; --i ) {
            stack.push( all[i] );
         }

         iter = iter.getSuperclass();
      }

      return stack.toArray( new JComponent[ stack.size() ]);
   }

   /**
    * Creates all inspectors which inspect the specified type.
    */
   protected <T> JComponent[] createInspectors( Class<?> clazz, T arg ) {
      List<Class<? extends JComponent>> inspectors = allInspectors.get( clazz );
      ArrayList<JComponent> created = new ArrayList<JComponent>();

      if( inspectors != null ) {

         for( Class<? extends JComponent> inspector : inspectors ) {
            JComponent instance = createInstance( inspector, arg );
            if( instance != null ) {
               created.add( instance );
            }
         }

      }

      return created.toArray( new JComponent[ created.size() ]);
   }

   /**
    * Creates an instance of the specified class by using the first 
    * constructor it finds which accepts the supplied argument. 
    */
   protected <T> JComponent createInstance( Class<? extends JComponent> clazz, T arg ) {
      Constructor<? extends JComponent> ctor = Reflecting.getMatchingConstructor( clazz, arg );

      if( ctor == null) { 
         return null; 
      }

      try { 
         return ctor.newInstance( arg ); 
      }
      catch( InvocationTargetException e ) { 
         throw new RuntimeException( e );
      }
      catch( IllegalAccessException e ) {
         throw new RuntimeException( e );
      }
      catch( InstantiationException e ) {
         throw new RuntimeException( e );
      } 
   }
}
