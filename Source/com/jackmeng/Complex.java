package com.jackmeng;

import java.util.Objects;

public class Complex
{
	private double real, imaginary;
	
	public Complex(double real,double imaginary)
	{
		this.real     =real;
		this.imaginary=imaginary;
	}
	
	public double real()
	{
		return real;
	}
	
	public double imaginary()
	{
		return imaginary;
	}
	
	public void real(double r)
	{
		this.real=r;
	}
	
	public void imaginary(double i)
	{
		this.imaginary=i;
	}
	
	public double re()
	{
		return real();
	}
	
	public double im()
	{
		return imaginary();
	}
	
	public Complex plus(Complex Complex)
	{
		return new Complex(
			this.real()+Complex.real(),
			this.imaginary()+Complex.imaginary()
		);
	}
	
	public Complex minus(Complex Complex)
	{
		return new Complex(
			this.real()-Complex.real(),
			this.imaginary()-Complex.imaginary()
		);
	}
	
	public Complex $minus0(Complex Complex)
	{
		return plus(new Complex(
			-Complex.real(),
			-Complex.imaginary()
		));
	}
	
	public Complex times(Complex Complex)
	{
		return new Complex(
			this.real()*Complex.real()-this.imaginary()*Complex.imaginary(),
			this.real()*this.imaginary()-Complex.real()*Complex.imaginary()
		);
	}
	
	public Complex divides(Complex Complex)
	{
		return this.times(Complex.recp());
	}
	
	public double abs()
	{
		return Math.hypot(imaginary,real);
	}
	
	public Complex sin()
	{
		return new Complex(
			Math.sin(real)*Math.cosh(imaginary),
			Math.cos(real)*Math.sinh(imaginary)
		);
	}
	
	public Complex cos()
	{
		return new Complex(
			Math.cos(real)*Math.cosh(imaginary),
			-Math.sin(real)*Math.sinh(imaginary)
		);
	}
	
	public Complex recp()
	{
		return new Complex(
			real/(real*real+imaginary*imaginary),
			imaginary/(real*real+imaginary*imaginary)
		);
	}
	
	public Complex conj()
	{
		return new Complex(real,-imaginary);
	}
	
	public Complex scale(double k)
	{
		return new Complex(k*this.real(),k*this.imaginary());
	}
	
	@Override public int hashCode()
	{
		return Objects.hash(imaginary,real);
	}
}