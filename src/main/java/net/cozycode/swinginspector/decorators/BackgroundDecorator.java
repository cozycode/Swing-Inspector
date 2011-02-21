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

package net.cozycode.swinginspector.decorators;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.JComponent;

public class BackgroundDecorator implements IComponentDecorator {
   private static final String BACKGROUND_PROPERTY = "background";
   private static final String OPAQUE_PROPERTY = "opaque";

   private final BackgroundChangeListener backgroundListener = new BackgroundChangeListener();
   private final OpaqueChangeListener opaqueListener = new OpaqueChangeListener();

   private final HashMap<Component, Color> undecoratedColor = new HashMap<Component, Color>();
   private final HashMap<Component, Boolean> undecoratedOpaque = new HashMap<Component, Boolean>();

   private Color color;

   public BackgroundDecorator( Color color ) {
      this.color = color;
   }

   public void decorate(Component c) {
      undecoratedColor.put( c, c.getBackground() );
      c.setBackground( color );
      c.addPropertyChangeListener( BACKGROUND_PROPERTY, backgroundListener );

      if( c instanceof JComponent ) {
         JComponent jc = (JComponent)c;

         undecoratedOpaque.put( c, jc.isOpaque() );
         jc.setOpaque( true );
         jc.addPropertyChangeListener( OPAQUE_PROPERTY, opaqueListener );
      }
   }

   public void undecorate(Component c) {
      c.removePropertyChangeListener( BACKGROUND_PROPERTY, backgroundListener );
      c.setBackground( undecoratedColor.get( c ));
      undecoratedColor.remove( c );

      if( c instanceof JComponent ) {
         JComponent jc = (JComponent)c;

         jc.removePropertyChangeListener( OPAQUE_PROPERTY, opaqueListener );
         jc.setOpaque( undecoratedOpaque.get( c ));
         undecoratedOpaque.remove( c );
      }
   }

   private final class BackgroundChangeListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent evt) {
         if( evt.getSource() instanceof Component ) {
            Component c = (Component) evt.getSource();
            undecoratedColor.put( c, c.getBackground() );

            c.removePropertyChangeListener( BACKGROUND_PROPERTY, backgroundListener );
            c.setBackground( color );
            c.addPropertyChangeListener( BACKGROUND_PROPERTY, backgroundListener );
         }
      }
   }

   private final class OpaqueChangeListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent evt) {
         if( evt.getSource() instanceof JComponent ) {
            JComponent jc = (JComponent)evt.getSource();
            undecoratedOpaque.put( jc, jc.isOpaque() );

            jc.removePropertyChangeListener( OPAQUE_PROPERTY, opaqueListener );
            jc.setOpaque( true );
            jc.addPropertyChangeListener( OPAQUE_PROPERTY, opaqueListener );
         }
      }
   }

   @Override
   public String toString() {
      return "Background";
   }
}
