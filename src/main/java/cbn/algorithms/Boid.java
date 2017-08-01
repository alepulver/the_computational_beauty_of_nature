package cbn.algorithms;

// Boid

import java.util.Random;

import cbn.Viewer;

public class Boid {
	private static final int tailLen = 10;
	
	private static Random rnd;
	private static int rows;
	private static int cols;
	private static Viewer viewer;
	
	private static double viewA;
	private static double vAvoidA;
	private static double minV;
	private static double copyR;
	private static double centroidR;
	private static double avoidR;
	private static double vAvoidR;
	private static double copyW;
	private static double centroidW;
	private static double avoidW;
	private static double vAvoidW;
	private static double randW;
	private static double dt;
	private static double ddt;

	private static Boid[] boids;
	
	private static double nx;
	private static double ny;
	
	private double x;
	private double y;
	private double vx;
	private double vy;
	private double nvx;
	private double nvy;
	
	public static void initMisc (Random r, int rr, int cc, Viewer v,
								 double va, double vaa, double mv) {
		rnd     = r;
		rows    = rr;
		cols    = cc;
		viewer  = v;
		viewA   = va * Math.PI / 180.0;
		vAvoidA = vaa * Math.PI / 180.0; // not radians in boids.c !
		                                 // GWF - thanks Mike, I fixed it.
		minV    = mv;
	}
	
	public static void initRadii (double cr, double ccr, double ar, double vr) {
		copyR     = cr;
		centroidR = ccr;
		avoidR    = ar;
		vAvoidR   = vr;
	}
	
	public static void initWeights (double cw, double ccw, double aw, double vw,
									double rw) {
		copyW     = cw;
		centroidW = ccw;
		avoidW    = aw;
		vAvoidW   = vw;
		randW     = rw;
	}
	
	public static void initTime (double t, double tt) {
		dt  = t;
		ddt = tt;
	}
	
	public static void initPopulation (Boid[] b) {
		boids = b;
	}
		
	private static void normalize (double x, double y) {
		double l = len (x, y);
		if (l != 0.0) {
			nx = x/l;
			ny = y/l;
		}
	}

	private static double len (double x, double y) {
		return Math.sqrt (x*x + y*y);
	}
	
	private static double dist (double x1, double y1, double x2, double y2) {
		return len (x2-x1, y2-y1);
	}
	
	private static double dot (double x1, double y1, double x2, double y2) {
		return (x1*x2 + y1*y2);
	}
	
	public Boid() {
		x = Math.abs (rnd.nextInt() % cols);
		y = Math.abs (rnd.nextInt() % rows);
		vx = 2*rnd.nextDouble()-1;
		vy = 2*rnd.nextDouble()-1;
		normalize (vx, vy);   vx = nx;   vy = ny;
	}
	
	public void computeNewHeading (int self) {
		int numcent = 0;
		double xa, ya, xb, yb, xc, yc, xd, yd, xt, yt;
		double mindist, mx=0, my=0, d;
		double cosangle, cosvangle, costemp;
		double xtemp, ytemp, maxr, u, v;
		double ss;
				
		maxr = Math.max (vAvoidR,
						 Math.max (copyR,
						 		   Math.max (centroidR, avoidR)));
		cosangle = Math.cos (viewA / 2);
		cosvangle = Math.cos (vAvoidA / 2);
		xa=ya=xb=yb=xc=yc=xd=yd=0;
		
		int numBoids = boids.length;
		for (int b=0; b<numBoids; b++) {
			if (b == self) continue;
			
			mindist = 10e10;
			for (int j=-cols; j<=cols; j+=cols)
				for (int k=-rows; k<=rows; k+=rows) {
					d = dist (boids[b].x+j, boids[b].y+k, x, y);
					if (d < mindist) {
						mindist = d;
						mx = boids[b].x+j;
						my = boids[b].y+k;
					}
				}
				
			if (mindist > maxr) continue;
			
			xtemp = mx-x;   ytemp = my-y;
			costemp = dot (vx, vy, xtemp, ytemp) / 
						(len (vx, vy) * len (xtemp, ytemp));
			if (costemp < cosangle) continue;
			
			if ((mindist <= centroidR) && (mindist > avoidR)) {
				xa += mx-x;
				ya += my-y;
				numcent++;
			}
			
			if ((mindist <= copyR) && (mindist > avoidR)) {
				xb += boids[b].vx;
				yb += boids[b].vy;
			}
			
			if (mindist <= avoidR) {
				xtemp = x-mx;
				ytemp = y-my;
				d = 1 / len (xtemp, ytemp);
				xtemp *= d;
				ytemp *= d;
				xc += xtemp;
				yc += ytemp;
			}
			
			if ((mindist <= vAvoidR) && (cosvangle < costemp)) {
				xtemp = x-mx;
				ytemp = y-my;
				
				u=v=0;
				if ((xtemp != 0) && (ytemp != 0)) {
					ss = (ytemp/xtemp);
					ss *= ss;
					u = Math.sqrt (ss / (1+ss));
					v = -xtemp * u/ytemp;
				} else if (xtemp != 0) {
					u=1;
				} else if (ytemp != 0) {
					v=1;
				}
				if ((vx*u + vy*v) < 0) {
					u = -u;
					v = -v;
				}
				
				u = x - mx + u;
				v = y - my + v;
				
				d = len (xtemp, ytemp);
				if (d != 0) {
					u /= d;
					v /= d;
				}
				xd += u;
				yd += v;
			}
		}
		
		if (numcent < 2) xa = ya = 0;
		
		if (len (xa, ya) > 1.0) { normalize (xa, ya);  xa = nx;  ya = ny; }
		if (len (xb, yb) > 1.0) { normalize (xb, yb);  xb = nx;  yb = ny; }
		if (len (xc, yc) > 1.0) { normalize (xc, yc);  xc = nx;  yc = ny; }
		if (len (xd, yd) > 1.0) { normalize (xd, yd);  xd = nx;  yd = ny; }
		
		xt = centroidW*xa + copyW*xb + avoidW*xc + vAvoidW*xd;
		yt = centroidW*ya + copyW*yb + avoidW*yc + vAvoidW*yd;
		
		if (randW > 0) {
			xt += randW * (2*rnd.nextDouble() - 1);
			yt += randW * (2*rnd.nextDouble() - 1);
		}
		
		nvx = vx*ddt + xt*(1-ddt);
		nvy = vy*ddt + yt*(1-ddt);
		d = len (nvx, nvy);
		if (d < minV) {
			nvx *= minV/d;
			nvy *= minV/d;
		}
	}
	
	public void update() {
		draw (false);
		
		vx = nvx;
		vy = nvy;
		x += vx*dt;
		y += vy*dt;
		
		if (x < 0) x += cols;
		else if (x >= cols) x -= cols;
		if (y < 0) y += rows;
		else if (y >= rows)	y -= rows;
		
		draw (true);
	}
	
	private void draw (boolean on) {
		double x1, x2, x3, y1, y2, y3, a, t, aa;
		int color = on ? 1 : 0;
		
	// direction line
	
		x3 = vx;
		y3 = vy;
		normalize (x3, y3);   x3 = nx;   y3 = ny;
		x1 = x;
		y1 = y;
		x2 = x1 - x3*tailLen;
		y2 = y1 - y3*tailLen;
		viewer.updateLine ((int) x1, (int) y1, (int) x2, (int) y2, color);
	
	// head
	
		t = (x1-x2) / tailLen;
		t = (t < -1) ? -1 : (t > 1) ? 1 : t;
		a = Math.acos (t);
		a = (y1-y2) < 0 ? -a : a;
	
	// head	(right)
	
		aa = a + viewA/2;
		x3 = x1 + Math.cos (aa) * tailLen / 3.0;
		y3 = y1 + Math.sin (aa) * tailLen / 3.0;
		viewer.updateLine ((int) x1, (int) y1, (int) x3, (int) y3, color);
	
	// head	(left)
		aa = a - viewA/2;
		x3 = x1 + Math.cos (aa) * tailLen / 3.0;
		y3 = y1 + Math.sin (aa) * tailLen / 3.0;
		viewer.updateLine ((int) x1, (int) y1, (int) x3, (int) y3, color);	
	}
}
