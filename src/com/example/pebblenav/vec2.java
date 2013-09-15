package com.example.pebblenav;

public class vec2 {
	public double x;
	public double y;
	
	public vec2(double a,double b)
	{
		x=a;
		y=b;
	}
	public vec2 scale(double c)
	{
		return new vec2(x*c,y*c);
	}
	public double magnitude()
	{
		return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
	}
	public double mag2()
	{
		return Math.pow(x, 2)+Math.pow(y, 2);
	}
	public vec2 add(vec2 a)
	{
		return new vec2(x+a.x,y+a.y);
	}
	public vec2 sub(vec2 a)
	{
		return new vec2(x-a.x,y-a.y);
	}
	public double dot(vec2 a)
	{
		return a.x*x+a.y*y;
	}
	
}
