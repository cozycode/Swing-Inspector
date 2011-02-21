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

import static net.cozycode.swinginspector.components.SwingInspectorUtilities.*;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.cozycode.constructs.IClosure;
import net.cozycode.constructs.Tuple;
import net.cozycode.constructs.Tuple2;
import net.cozycode.swing.formatters.IntegerFormatter;
import net.cozycode.swing.listeners.FocusListeners;
import net.miginfocom.swing.MigLayout;

public class InsetsPanel extends JPanel {
   private static final long serialVersionUID = 1L;

   private final MyDocumentListener documentListener = new MyDocumentListener();
   private final MyFocusListener focusListener = new MyFocusListener();
   private final MyActionListener actionListener = new MyActionListener();
   private final IClosure<Tuple2<InsetsPanel,Insets>> closure;

   private final JFormattedTextField top
      = destylize( new JFormattedTextField( new IntegerFormatter( false )));

   private final JFormattedTextField left
      = destylize( new JFormattedTextField( new IntegerFormatter( false )));

   private final JFormattedTextField bottom
      = destylize( new JFormattedTextField( new IntegerFormatter( false )));

   private final JFormattedTextField right
      = destylize( new JFormattedTextField( new IntegerFormatter( false )));

   public InsetsPanel() {
      this( null );
      top.setEditable( false );
      bottom.setEditable( false );
      left.setEditable( false );
      right.setEditable( false );

      top.setFocusable( false );
      bottom.setFocusable( false );
      left.setFocusable( false );
      right.setFocusable( false );
   }

   boolean updating = false; 

   public InsetsPanel( IClosure<Tuple2<InsetsPanel,Insets>> closure ) {
      super( new MigLayout( "insets 0" ));
      this.closure = closure;

      setToolTipText( "[top, left, bottom, right]" );
      top.setToolTipText( "[top, left, bottom, right]" );
      left.setToolTipText( "[top, left, bottom, right]" );
      bottom.setToolTipText( "[top, left, bottom, right]" );
      right.setToolTipText( "[top, left, bottom, right]" );

      add( mildFont( new JLabel( "[" )));
      add( top );
      add( mildFont( new JLabel( "," )));
      add( left );
      add( mildFont( new JLabel( "," )));
      add( bottom );
      add( mildFont( new JLabel( "," )));
      add( right );
      add( mildFont( new JLabel( "]" )));

      top.addFocusListener( focusListener );
      left.addFocusListener( focusListener );
      bottom.addFocusListener( focusListener );
      right.addFocusListener( focusListener );

      top.addFocusListener( FocusListeners.SELECT_ALL );
      left.addFocusListener( FocusListeners.SELECT_ALL );
      bottom.addFocusListener( FocusListeners.SELECT_ALL );
      right.addFocusListener( FocusListeners.SELECT_ALL );

      top.addActionListener( actionListener );
      left.addActionListener( actionListener );
      bottom.addActionListener( actionListener );
      right.addActionListener( actionListener );

      top.getDocument().addDocumentListener( documentListener );
      left.getDocument().addDocumentListener( documentListener );
      bottom.getDocument().addDocumentListener( documentListener );
      right.getDocument().addDocumentListener( documentListener );
   }

   public void setInsets( Insets insets ) {
      if( !updating ) {
         updating = true;

         top.setValue( insets.top );
         left.setValue( insets.left );
         bottom.setValue( insets.bottom );
         right.setValue( insets.right );

         updating = false;
      }
   }

   private void textChanged() {
      if( closure != null && !updating ) {
         updating = true;

         int t = (Integer)top.getValue();
         int l = (Integer)left.getValue();
         int b = (Integer)bottom.getValue();
         int r = (Integer)right.getValue();
         closure.yield( Tuple.tuple( this, new Insets( t, l, b, r )));

         updating = false;
      }
   }

   private class MyActionListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         textChanged();
      }
   }

   private class MyFocusListener implements FocusListener {
      @Override
      public void focusGained(FocusEvent e) { /* noop */ }

      @Override
      public void focusLost(FocusEvent e) {
         // This corrects an order of operations issue
         // where the focus handler is running before
         // the field has a chance to commit the edit.
         SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
               textChanged();
            }
         });
      }
   }

   private class MyDocumentListener implements DocumentListener {
      @Override
      public void insertUpdate(DocumentEvent e) {
         revalidate();
         repaint();     
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
         revalidate();
         repaint();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
         revalidate();
         repaint();
      }
   }
}
