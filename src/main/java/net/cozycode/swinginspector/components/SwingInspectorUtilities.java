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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public final class SwingInspectorUtilities {
   private SwingInspectorUtilities() { /* Static Singleton */ }

   public static <T extends JComponent> T mildFont( T comp ) {
      comp.setFont( comp.getFont().deriveFont( Font.PLAIN, 12f ));
      return comp;
   }

   public static <T extends JComponent> T strongFont( T comp ) {
      comp.setFont( comp.getFont().deriveFont( Font.BOLD, 12f ));
      return comp;
   }

   public static void addTo( JPanel panel, String label, String value ) {
      JLabel lhs = strongFont( new JLabel( label ));
      JLabel rhs = mildFont( new JLabel( value ));
      addTo( panel, lhs, rhs );
   }

   public static void addTo( JPanel panel, String label, JLabel value ) {
      JLabel lhs = strongFont( new JLabel( label ));
      JLabel rhs = mildFont( value );
      addTo( panel, lhs, rhs );
   }

   public static void addTo( JPanel panel, String label, JComponent comp ) {
      JLabel lbl = strongFont( new JLabel( label ));
      addTo( panel, lbl, comp );
   }

   public static void addTo( JPanel panel, JComponent lhs, JComponent rhs ) {
      panel.add( lhs );
      panel.add( rhs );
   }

   public static void addSpacer( JPanel panel ) {
      JPanel filler = new JPanel();
      filler.setOpaque( false );
      filler.setPreferredSize( new Dimension( 0, 10 ));
      panel.add( filler, "span" );
   }

   public static Point dimensionToPoint( Dimension d ) {
      return d != null ? new Point( d.width, d.height ) : null;
   }

   public static Dimension pointToDimension( Point p ) {
      return p != null ? new Dimension( p.x, p.y ) : null;
   }

   public static <T extends JComponent> T strip( T comp ) {
      comp.setBackground( null );
      comp.setForeground( null );
      comp.setBorder( null );
      return comp;
   }

   public static void revalidateContainer( Component comp ) {
      Container container = comp.getParent();
      if( container instanceof JComponent ) {
         ((JComponent)container).revalidate();
      }
   }

   public static <T extends JComponent> T destylize( T comp ) {
      // The textUI for the nimbus look and feel doesn't match the panel
      // background color when you set it to null.  Furthermore, if
      // the background color is a ColorUIResource or UIResource
      // it is ignored in favor of whatever nimbus prefers to do.
      //
      // We work around this by obtaining the panel background and
      // foreground colors and using them to make plain colors using
      // the values we want.

      Color c = UIManager.getColor( "Panel.background" );
      Color background = new Color( c.getRed(), c.getGreen(), c.getBlue() );

      c = UIManager.getColor( "Panel.foreground" );
      Color foreground = new Color( c.getRed(), c.getGreen(), c.getBlue() );

      comp.setBackground( background );
      comp.setForeground( foreground );
      comp.setBorder( null );
      return comp;
   }
}
