package org.roger600.lienzo.client.layer;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.json.JSONDeserializer;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.NodeType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class PointLayer extends Layer {
    private static final int X = 0;
    private static final int Y = 1;
    private static final int PRIMARY_X = 0;
    private static final int PRIMARY_Y = 1;
    private static final int SECONDARY_X = 2;
    private static final int SECONDARY_Y = 3;
    private double[] m_sizes;
    private Circle[] m_lines;

    public PointLayer() {
        this.m_sizes = new double[]{10.0D, 10.0D, 5.0D, 5.0D};
        this.m_lines = new Circle[4];
        this.setNodeType(PointLayerNodeType.POINT_GRID_TYPE);
    }

    public PointLayer(double size, Circle line) {
        this.m_sizes = new double[]{10.0D, 10.0D, 5.0D, 5.0D};
        this.m_lines = new Circle[4];
        this.setNodeType(PointLayerNodeType.POINT_GRID_TYPE);
        this.setPrimarySizeX(size);
        this.setPrimarySizeY(size);
        this.setPrimaryLineX(line);
        this.setPrimaryLineY(line);
    }

    public PointLayer(double primarySize, Circle primaryLine, double secondarySize, Circle secondaryLine) {
        this(primarySize, primaryLine);
        this.setSecondarySizeX(secondarySize);
        this.setSecondarySizeY(secondarySize);
        this.setSecondaryLineX(secondaryLine);
        this.setSecondaryLineY(secondaryLine);
    }

    protected PointLayer(JSONObject node, ValidationContext ctx, Circle[] lines, double[] sizes) throws ValidationException {
        super(node, ctx);
        this.m_sizes = new double[]{10.0D, 10.0D, 5.0D, 5.0D};
        this.m_lines = new Circle[4];
        this.setNodeType(PointLayerNodeType.POINT_GRID_TYPE);
        this.m_lines = lines;
        this.m_sizes = sizes;
    }

    public double getPrimarySizeX() {
        return this.m_sizes[0];
    }

    public PointLayer setPrimarySizeX( double primaryX) {
        this.m_sizes[0] = primaryX;
        return this;
    }

    public double getPrimarySizeY() {
        return this.m_sizes[1];
    }

    public PointLayer setPrimarySizeY(double primaryY) {
        this.m_sizes[1] = primaryY;
        return this;
    }

    public Circle getPrimaryLineX() {
        return this.m_lines[0];
    }

    public PointLayer setPrimaryLineX(Circle primaryLineX) {
        this.m_lines[0] = primaryLineX;
        return this;
    }

    public Circle getPrimaryLineY() {
        return this.m_lines[1];
    }

    public PointLayer setPrimaryLineY(Circle primaryLineY) {
        this.m_lines[1] = primaryLineY;
        return this;
    }

    public double getSecondarySizeX() {
        return this.m_sizes[2];
    }

    public PointLayer setSecondarySizeX(double secondaryX) {
        this.m_sizes[2] = secondaryX;
        return this;
    }

    public double getSecondarySizeY() {
        return this.m_sizes[3];
    }

    public PointLayer setSecondarySizeY(double secondaryY) {
        this.m_sizes[3] = secondaryY;
        return this;
    }

    public Circle getSecondaryLineX() {
        return this.m_lines[2];
    }

    public PointLayer setSecondaryLineX(Circle secondaryLineX) {
        this.m_lines[2] = secondaryLineX;
        return this;
    }

    public Circle getSecondaryLineY() {
        return this.m_lines[3];
    }

    public void setSecondaryLineY(Circle secondaryLineY) {
        this.m_lines[3] = secondaryLineY;
    }

    private static final int RADIUS = 5;

    protected void drawWithoutTransforms(Context2D context, double alpha, BoundingBox bounds) {
        if(this.isVisible()) {
            Viewport vp = this.getViewport();
            int vw = vp.getWidth();
            int vh = vp.getHeight();
            Point2D a = new Point2D(0.0D, 0.0D);
            Point2D b = new Point2D((double)vw, (double)vh);
            double scaleX = 1.0D;
            double scaleY = 1.0D;
            Transform t = this.isTransformable()?vp.getTransform():null;
            if(t != null) {
                scaleX = t.getScaleX();
                scaleY = t.getScaleY();
                t = t.getInverse();
                t.transform(a, a);
                t.transform(b, b);
            }

            double x1 = a.getX();
            double y1 = a.getY();
            double x2 = b.getX();
            double y2 = b.getY();

            for(int direction = 0; direction <= 1; ++direction) {
                boolean vertical = direction == 0;
                double scale = vertical?scaleX:scaleY;
                double min = vertical?x1:y1;
                double max = vertical?x2:y2;

                for(int primSec = 0; primSec <= 1; ++primSec) {
                    int index = primSec * 2 + direction;
                    boolean isSecondary = primSec == 1;
                    if(this.m_lines[index] != null) {
                        int n = 0;
                        if(isSecondary) {
                            n = (int)Math.round(this.m_sizes[direction] / this.m_sizes[index]);
                        }

                        Circle line = this.m_lines[index];
                        GWT.log("Next circle");
                        double size = this.m_sizes[index];
                        double previousLineWidth = line.getStrokeWidth();
                        // line.setStrokeWidth(previousLineWidth / scale);
                        DashArray previousDashes = line.getDashArray();
                        if(previousDashes != null) {
                            double[] n1 = previousDashes.getNormalizedArray();
                            DashArray dashes = new DashArray();

                            for(int n2 = 0; n2 < n1.length; ++n2) {
                                dashes.push(n1[n2] / scale);
                            }

                            // line.setDashArray(dashes);
                        }

                        long var53 = Math.round(min / size);
                        if((double)var53 * size < min) {
                            ++var53;
                        }

                        long var52 = Math.round(max / size);
                        if((double)var52 * size > max) {
                            --var52;
                        }


                        /*
                        Point2DArray points = line.getPoints();
                        Point2D p1 = points.get(0);
                        Point2D p2 = points.get(1);
                        if(vertical) {
                            p1.setY(y1);
                            p2.setY(y2);
                        } else {
                            p1.setX(x1);
                            p2.setX(x2);
                        }
                         */
                        line.setRadius( RADIUS );
                        if ( vertical ) {
                            final double d = y1 + ( y2 - y1 / 2 );
                            line.setY( d );
                            GWT.log("Y="+d);

                        } else {
                            final double d = x1 + ( x2 - x1 / 2 );
                            line.setX( d );
                            GWT.log("X="+d);
                        }




                        /*
                        for(long ni = var53; ni <= var52; ++ni) {
                            if(!isSecondary || ni % (long)n != 0L) {
                                double y;
                                if(vertical) {
                                    y = (double)ni * size;
                                    p1.setX(y);
                                    p2.setX(y);
                                } else {
                                    y = (double)ni * size;
                                    p1.setY(y);
                                    p2.setY(y);
                                }

                                line.drawWithTransforms(context, alpha, bounds);
                            }
                        }
                         */

                        for(long ni = var53; ni <= var52; ++ni) {
                            if ( !isSecondary || ni % ( long ) n != 0L ) {
                                double y;
                                if ( vertical ) {
                                    y = ( double ) ni * size;
                                    line.setX( y );
                                    GWT.log( "X=" + y );
                                } else {
                                    y = ( double ) ni * size;
                                    line.setY( y );
                                    GWT.log( "Y=" + y );
                                }
                                GWT.log( "Radius=" + line.getRadius() );
                                GWT.log( "Alpha=" + alpha );
                                /*new Circle( 5 )
                                        .setFillColor( ColorName.BLACK )
                                        .setAlpha( 1 )
                                        .setX( line.getX() )
                                        .setY( line.getY() )
                                        .drawWithTransforms( context, alpha, bounds );*/
                                line.drawWithTransforms( context, alpha, bounds );
                                super.drawWithTransforms(context, alpha, bounds);
                            }
                        }

                        // line.setStrokeWidth(previousLineWidth);
                        if(previousDashes != null) {
                            // line.setDashArray(previousDashes);
                        }
                    }
                }
            }

            super.drawWithoutTransforms(context, alpha, bounds);
        }
    }

    public JSONObject toJSONObject() {
        JSONObject obj = super.toJSONObject();
        JSONArray lines = new JSONArray();
        JSONArray sizes = new JSONArray();

        for(int i = 0; i < 4; ++i) {
            if(this.m_lines[i] == null) {
                lines.set(i, JSONNull.getInstance());
            } else {
                lines.set(i, this.m_lines[i].toJSONObject());
            }

            sizes.set(i, new JSONNumber(this.m_sizes[i]));
        }

        obj.put("lines", lines);
        obj.put("sizes", sizes);
        return obj;
    }

    public static class PointLayerNodeType extends NodeType {
        public static final PointLayerNodeType POINT_GRID_TYPE = new PointLayerNodeType();
        protected PointLayerNodeType() {
            super( "PointLayer" );
        }
    }

    public static class PointLayerFactory extends LayerFactory {
        public PointLayerFactory() {
            this.setNodeType( PointLayerNodeType.POINT_GRID_TYPE );
        }

        public PointLayer container(JSONObject node, ValidationContext ctx) throws ValidationException {
            Circle[] lines = new Circle[4];
            double[] sizes = new double[]{10.0D, 10.0D, 5.0D, 5.0D};
            JSONValue aval = node.get("lines");
            JSONArray arr;
            int i;
            JSONValue jval;
            if(aval != null) {
                arr = aval.isArray();
                if(arr != null) {
                    for(i = 0; i < 4 && i < arr.size(); ++i) {
                        jval = arr.get(i);
                        if(jval != null) {
                            JSONObject jnum = jval.isObject();
                            if(jnum != null) {
                                Circle line = (Circle)JSONDeserializer.get().fromJSON(jnum, ctx);
                                lines[i] = line;
                            }
                        }
                    }
                }
            }

            aval = node.get("sizes");
            if(aval != null) {
                arr = aval.isArray();
                if(arr != null) {
                    for(i = 0; i < 4 && i < arr.size(); ++i) {
                        jval = arr.get(i);
                        if(jval != null) {
                            JSONNumber var11 = jval.isNumber();
                            if(var11 != null) {
                                sizes[i] = var11.doubleValue();
                            }
                        }
                    }
                }
            }

            return new PointLayer(node, ctx, lines, sizes);
        }
    }
}
