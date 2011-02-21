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

package net.cozycode.swinginspector.inspectors;

import static net.cozycode.swinginspector.components.SwingInspectorUtilities.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.cozycode.constructs.IClosure;
import net.cozycode.constructs.Tuple2;
import net.cozycode.core.IDisposable;
import net.cozycode.swing.components.BooleanLabel;
import net.cozycode.swing.components.JTitledSeparator;
import net.cozycode.swing.formatters.FloatFormatter;
import net.cozycode.swinginspector.Inspector;
import net.cozycode.swinginspector.components.ColorPanel;
import net.cozycode.swinginspector.components.FontPanel;
import net.cozycode.swinginspector.components.PointPanel;
import net.miginfocom.swing.MigLayout;


@Inspector(Component.class)
public class ComponentInspector extends JPanel implements IDisposable {
   private static final long serialVersionUID = 1L;

   private final Component comp;

   private final CompFocusListener focusListener = new CompFocusListener();
   private final CompComponentListener componentListener = new CompComponentListener();
   private final CompPropertyChangeListener propertyListener = new CompPropertyChangeListener();
   private final CompHierarchyListener hierarchyListener = new CompHierarchyListener();
   private final CompHierarchyBoundsListener hierarchyBoundsListener = new CompHierarchyBoundsListener();

   private final FieldCheckBoxListener fieldCheckBoxListener = new FieldCheckBoxListener();
   private final FieldActionListener fieldActionListener = new FieldActionListener();
   private final FieldFocusListener fieldFocusListener = new FieldFocusListener();

   private final FontChangeListener fontListener = new FontChangeListener();
   private final ColorChangeListener colorListener = new ColorChangeListener();
   private final PointChangeListener pointListener = new PointChangeListener();

   private final JLabel name = new JLabel();
   private final JLabel locale = new JLabel();

   private final PointPanel location = new PointPanel( pointListener );
   private final PointPanel locationOnScreen = new PointPanel( pointListener );

   private final PointPanel size = new PointPanel( pointListener );
   private final PointPanel minSize = new PointPanel( pointListener, true );
   private final PointPanel maxSize = new PointPanel( pointListener, true );
   private final PointPanel prefSize = new PointPanel( pointListener, true );

   private final FontPanel font = new FontPanel( fontListener, true );
   private final ColorPanel foreground = new ColorPanel( colorListener, true );
   private final ColorPanel background = new ColorPanel( colorListener, true );

   private final JFormattedTextField alignmentX 
      = strip( new JFormattedTextField(  new FloatFormatter( false )));
   private final JFormattedTextField alignmentY 
      = strip( new JFormattedTextField(  new FloatFormatter( false )));

   private final JCheckBox enabled = new JCheckBox();
   private final JCheckBox visible = new JCheckBox();
   private final JCheckBox opaque = new JCheckBox();
   private final JCheckBox focusable = new JCheckBox();
   private final JCheckBox focusTraversalKeysEnabled = new JCheckBox();
   private final JCheckBox ignoreRepaint = new JCheckBox();

   private final BooleanLabel showing = new BooleanLabel();
   private final BooleanLabel focusOwner = new BooleanLabel();
   private final BooleanLabel doubleBuffered = new BooleanLabel();
   private final BooleanLabel lightweight = new BooleanLabel();
   private final JLabel componentOrientation = new JLabel();

   private final BooleanLabel isFontSet = new BooleanLabel();
   private final BooleanLabel isForegroundSet = new BooleanLabel();
   private final BooleanLabel isBackgroundSet = new BooleanLabel();
   private final BooleanLabel isMinSizeSet = new BooleanLabel();
   private final BooleanLabel isMaxSizeSet = new BooleanLabel();
   private final BooleanLabel isPrefSizeSet = new BooleanLabel();
   private final BooleanLabel isCursorSet = new BooleanLabel();


   public ComponentInspector( Component comp ) {
      super( new MigLayout( "wrap, fill", "[fill]" ));
      this.comp = comp;

      comp.addFocusListener( focusListener );
      comp.addComponentListener( componentListener );
      comp.addPropertyChangeListener( propertyListener );
      comp.addHierarchyListener( hierarchyListener );
      comp.addHierarchyBoundsListener( hierarchyBoundsListener );

      enabled.addActionListener( fieldCheckBoxListener );
      visible.addActionListener( fieldCheckBoxListener );
      opaque.addActionListener( fieldCheckBoxListener );
      focusable.addActionListener( fieldCheckBoxListener );
      focusTraversalKeysEnabled.addActionListener( fieldCheckBoxListener );
      ignoreRepaint.addActionListener( fieldCheckBoxListener );

      if( comp instanceof JComponent ) {
         alignmentX.addFocusListener( fieldFocusListener );
         alignmentY.addFocusListener( fieldFocusListener );
         alignmentX.addActionListener( fieldActionListener );
         alignmentY.addActionListener( fieldActionListener );
      }
      else {
         opaque.setEnabled( false );
         alignmentX.setEnabled( false );
         alignmentY.setEnabled( false );
      }

      locationOnScreen.setEnabled( false );

      add( new JTitledSeparator( "Component" ) );
      add( createContent() );
      updateValues();
   }

   public void dispose() {
      comp.removeFocusListener( focusListener );
      comp.removeComponentListener( componentListener );
      comp.removePropertyChangeListener( propertyListener );
      comp.removeHierarchyListener( hierarchyListener );
      comp.removeHierarchyBoundsListener( hierarchyBoundsListener );
   }

   private JComponent createContent() {
      JPanel pane = new JPanel( new MigLayout(  
         "wrap, fill, insets 0 10 0 0", 
         "[align right]6[grow,fill]",
         "[align top]"
      ));

      addTo( pane, "Name:", name  );
      addTo( pane, "Locale:", locale  );
      addSpacer( pane );

      addTo( pane, "Location:", location );
      addTo( pane, "Location on Screen:", locationOnScreen );
      addSpacer( pane );

      addTo( pane, "Size:", size );
      addTo( pane, "Minimum Size:", minSize );
      addTo( pane, "Maximum Size:", maxSize );
      addTo( pane, "Preferred Size:", prefSize );
      addSpacer( pane );

      addTo( pane, "Font:", font );
      addSpacer( pane );

      addTo( pane, "Foreground:", foreground );
      addTo( pane, "Background:", background );
      addSpacer( pane );

      addTo( pane, "AlignmentX:", alignmentX );
      addTo( pane, "AlignmentY:", alignmentY );
      addSpacer( pane );

      addTo( pane, "Enabled:", enabled );
      addTo( pane, "Visible:", visible );
      addTo( pane, "Opaque:", opaque );
      addTo( pane, "Focusable:", focusable );
      addTo( pane, "Focus Traversal Keys Enabled:", focusTraversalKeysEnabled );
      addTo( pane, "Ignore Repaint:", ignoreRepaint );
      addSpacer( pane );

      addTo( pane, "Showing:", showing );
      addTo( pane, "Focus Owner:", focusOwner );
      addTo( pane, "Double Buffered:", doubleBuffered );
      addTo( pane, "Lightweight:", lightweight );
      addTo( pane, "Component Orientation:", componentOrientation );
      addSpacer( pane );

      addTo( pane, "Is Font Set:", isFontSet );
      addTo( pane, "Is Foreground Set:", isForegroundSet );
      addTo( pane, "Is Background Set:", isBackgroundSet );
      addTo( pane, "Is Minimum Size Set:", isMinSizeSet );
      addTo( pane, "Is Maximum Size Set:", isMaxSizeSet );
      addTo( pane, "Is Preferred Size Set:", isPrefSizeSet );
      addTo( pane, "Is Cursor Set:", isCursorSet );
      addSpacer( pane );

      return pane;
   }

   private void updateValues() {
      name.setText( comp.getName() );
      locale.setText( comp.getLocale().toString() );

      location.setPoint( comp.getLocation() );
      if( comp.isShowing() ) {
         locationOnScreen.setPoint( comp.getLocationOnScreen() );
      }

      size.setPoint( dimensionToPoint( comp.getSize() ));
      minSize.setPoint( dimensionToPoint( comp.getMinimumSize() ));
      maxSize.setPoint( dimensionToPoint( comp.getMaximumSize() ));
      prefSize.setPoint( dimensionToPoint( comp.getPreferredSize() ));

      font.setFontValue( comp.getFont() );
      foreground.setColor( comp.getForeground() );
      background.setColor( comp.getBackground() );

      alignmentX.setValue( comp.getAlignmentX() );
      alignmentY.setValue( comp.getAlignmentY() );

      enabled.setSelected( comp.isEnabled() );
      visible.setSelected( comp.isVisible() );
      opaque.setSelected( comp.isOpaque() );
      focusable.setSelected( comp.isFocusable() );
      focusTraversalKeysEnabled.setSelected( comp.getFocusTraversalKeysEnabled());
      ignoreRepaint.setSelected( comp.getIgnoreRepaint() );

      showing.setBoolean( comp.isShowing() );
      focusOwner.setBoolean( comp.isFocusOwner() );
      doubleBuffered.setBoolean( comp.isDoubleBuffered() );
      lightweight.setBoolean( comp.isLightweight() );
      componentOrientation.setText( orientationToString( comp.getComponentOrientation() ));

      isFontSet.setBoolean( comp.isFontSet() );
      isBackgroundSet.setBoolean( comp.isBackgroundSet() );
      isForegroundSet.setBoolean( comp.isForegroundSet() );
      isMinSizeSet.setBoolean( comp.isMinimumSizeSet() );
      isMaxSizeSet.setBoolean( comp.isMaximumSizeSet() );
      isPrefSizeSet.setBoolean( comp.isPreferredSizeSet() );
      isCursorSet.setBoolean( comp.isCursorSet() );
   }

   private static String orientationToString( ComponentOrientation orientation ) {
      if( ComponentOrientation.LEFT_TO_RIGHT == orientation ) return "Left to Right";
      if( ComponentOrientation.RIGHT_TO_LEFT == orientation ) return "Right to Left";
      if( ComponentOrientation.UNKNOWN == orientation ) return "Unknown";
      if( null == orientation ) return "null";
      return "???";
   }

   private final class FontChangeListener implements IClosure<Tuple2<FontPanel,Font>> {
      @Override
      public void yield(Tuple2<FontPanel, Font> item) {
         if( font == item.getFirst() ) {
            comp.setFont( item.getSecond() );
            revalidateContainer( comp );
            comp.repaint();
         }
      }
   }

   private final class ColorChangeListener implements IClosure<Tuple2<ColorPanel,Color>> {
      @Override
      public void yield(Tuple2<ColorPanel, Color> item) {
         if( foreground == item.getFirst() ) {
            comp.setForeground( item.getSecond() );
         }
         if( background == item.getFirst() ) {
            comp.setBackground( item.getSecond() );
         }
      }
   }

   private final class PointChangeListener implements IClosure<Tuple2<PointPanel,Point>> {
      @Override
      public void yield(Tuple2<PointPanel, Point> item) {
         if( location == item.getFirst() ) {
            comp.setLocation( item.getSecond() );
         }
         if( size == item.getFirst() ) {
            comp.setSize( pointToDimension( item.getSecond() ));
         }
         if( minSize == item.getFirst() ) {

            //This avoids replacing a null (unset) minimum size
            //with a new Dimension object if no actual change
            //to the underlying dimension has been made.
            Dimension d = pointToDimension( item.getSecond() );
            if( !comp.getMinimumSize().equals( d )) {
               comp.setMinimumSize( d );
            }
         }
         if( maxSize == item.getFirst() ) {

            //This avoids replacing a null (unset) maximum size
            //with a new Dimension object if no actual change
            //to the underlying dimension has been made.
            Dimension d = pointToDimension( item.getSecond() );
            if( !comp.getMaximumSize().equals( d )) {
               comp.setMaximumSize( d );
            }
         }
         if( prefSize == item.getFirst() ) {
            //This avoids replacing a null (unset) preferred size
            //with a new Dimension object if no actual change
            //to the underlying dimension has been made.
            Dimension d = pointToDimension( item.getSecond() );
            if( !comp.getPreferredSize().equals( d )) {
               comp.setPreferredSize( d );
            }
         }
         revalidateContainer( comp );
         comp.repaint();
      }
   }

   private final class FieldCheckBoxListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         if( enabled == e.getSource() ) {
            comp.setEnabled( enabled.isSelected() );
         }
         if( visible == e.getSource() ) {
            comp.setVisible( visible.isSelected() );
         }
         if( focusable == e.getSource() ) {
            comp.setFocusable( focusable.isSelected() );
         }
         if( focusTraversalKeysEnabled == e.getSource() ) {
            comp.setFocusTraversalKeysEnabled( focusTraversalKeysEnabled.isSelected() );
         }
         if( ignoreRepaint == e.getSource() ) {
            comp.setIgnoreRepaint( ignoreRepaint.isSelected() );
         }

         if( comp instanceof JComponent ) {
            JComponent jcomp = (JComponent)comp;

            if( opaque == e.getSource() ) {
               jcomp.setOpaque( opaque.isSelected() );
            }
         }

         revalidateContainer( comp );
         comp.repaint();
      }
   }

   private final class CompComponentListener implements ComponentListener {
      public void componentResized(ComponentEvent e) {
         updateValues();
      }

      public void componentMoved(ComponentEvent e) {
         updateValues();
      }

      public void componentShown(ComponentEvent e) {
         updateValues();
      }

      public void componentHidden(ComponentEvent e) {
         updateValues();
      }
   }

   private final class CompFocusListener implements FocusListener {
      @Override
      public void focusGained(FocusEvent e) {
         updateValues();
      }

      @Override
      public void focusLost(FocusEvent e) {
         updateValues();
      }
   }

   private final class FieldFocusListener implements FocusListener {
      @Override
      public void focusGained(FocusEvent e) { /* noop */ }

      @Override
      public void focusLost(FocusEvent e) {
         if( e.getSource() instanceof JComponent ) {
            if( alignmentX == e.getSource() ) {
               ((JComponent)comp).setAlignmentX( (Float)alignmentX.getValue() );
            }
            if( alignmentY == e.getSource() ) {
               ((JComponent)comp).setAlignmentY( (Float)alignmentY.getValue() );
            }
         }
      }
   }

   private final class FieldActionListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         if( alignmentX == e.getSource() ) {
            ((JComponent)comp).setAlignmentX( (Float)alignmentX.getValue() );
         }
         if( alignmentY == e.getSource() ) {
            ((JComponent)comp).setAlignmentY( (Float)alignmentY.getValue() );
         }
      }
   }

   private final class CompPropertyChangeListener implements PropertyChangeListener{
      public void propertyChange(PropertyChangeEvent evt) {
         updateValues();
         //		   System.out.println( "Property: "+ evt.getPropertyName() +" = "+ evt.getNewValue() );
      }
   }

   private final class CompHierarchyListener implements HierarchyListener {
      @Override
      public void hierarchyChanged(HierarchyEvent e) {
         updateValues();
      }
   }

   private final class CompHierarchyBoundsListener implements HierarchyBoundsListener {
      @Override
      public void ancestorMoved(HierarchyEvent e) {
         updateValues();
      }

      @Override
      public void ancestorResized(HierarchyEvent e) {
         updateValues();
      }
   }
}