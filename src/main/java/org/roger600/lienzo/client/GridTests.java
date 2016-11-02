package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.GridLayer;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.roger600.lienzo.client.layer.PointLayer;

public class GridTests implements EntryPoint {

    private final VerticalPanel mainPanel = new VerticalPanel();

    @Override
    public void onModuleLoad() {
        final LienzoPanel panel = new LienzoPanel(1200, 900);
        final Layer layer = new Layer().setTransformable( true );
        panel.add(layer);
        // applyLineGrid( panel );
        applyPointGrid( panel );
        mainPanel.add( panel );
        RootPanel.get().add( mainPanel );
    }

    private void applyLineGrid( final LienzoPanel panel ) {

        // Grid.
        Line line1 = new Line( 0, 0, 0, 0 )
                .setStrokeColor( "#0000FF" )
                .setAlpha( 0.2 );
        Line line2 = new Line( 0, 0, 0, 0 )
                .setStrokeColor( "#00FF00"  )
                .setAlpha( 0.2 );

        line2.setDashArray( 2,
                2 );

        GridLayer gridLayer = new GridLayer( 100, line1, 25, line2 );

        panel.setBackgroundLayer( gridLayer );
    }

    private void applyPointGrid( final LienzoPanel panel ) {

        // Grid.
        Circle c1 = new Circle( 5 )
                .setFillColor( "#0000FF" )
                .setStrokeColor( "#0000FF" )
                .setStrokeWidth( 10 )
                .setAlpha( 1 );
        Circle c2 = new Circle( 1 )
                .setFillColor( "#00FF00" )
                .setStrokeColor( "#00FF00" )
                .setStrokeWidth( 5 )
                .setAlpha( 1 );

        PointLayer gridLayer = new PointLayer( 100, c1, 25, c2 );

        panel.setBackgroundLayer( gridLayer );
    }

}
