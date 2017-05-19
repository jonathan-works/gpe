package br.com.infox.core.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SizeConverter {
	
	public static final int MULTIPLIER = 1024;
	
    public static NumberFormat FORMATTER = new DecimalFormat("###,##0.##");
	
	public static SizeConverterBuilder fromBytes(long bytes) {
		return new SizeConverterBuilderImpl().addBytes(bytes);
	}
	
	public static SizeConverterBuilder fromKB(double kb) {
		return new SizeConverterBuilderImpl().addKB(kb);		
	}
	
	public static SizeConverterBuilder fromMB(double mb) {
		return new SizeConverterBuilderImpl().addMB(mb);		
	}
	
	public static SizeConverterBuilder fromGB(double gb) {
		return new SizeConverterBuilderImpl().addGB(gb);		
	}
	public static SizeConverterBuilder fromTB(double tb) {
		return new SizeConverterBuilderImpl().addTB(tb);		
	}
	
	public interface SizeConverterBuilder extends SizeConverterOutput {
		SizeConverterBuilder addBytes(long bytes);
		SizeConverterBuilder addKB(double kb);
		SizeConverterBuilder addMB(double mb);
		SizeConverterBuilder addGB(double gb);
		SizeConverterBuilder addTB(double tb);
	}
	
	public interface SizeConverterOutput {
		long toBytes();
		double toKB();
		double toMB();
		double toGB();
		double toTB();
		String toString();
	}
	
	protected static class SizeConverterBuilderImpl implements SizeConverterBuilder {
		
		private long bytes;

		private SizeConverterBuilderImpl() {
			
		}
		
		@Override
		public SizeConverterBuilder addBytes(long bytes) {
			this.bytes += bytes;
			return this;
		}

		@Override
		public SizeConverterBuilder addKB(double kb) {
			addBytes((long)(kb * MULTIPLIER));
			return this;
		}

		@Override
		public SizeConverterBuilder addMB(double mb) {
			addKB((long)(mb * MULTIPLIER));
			return this;
		}

		@Override
		public SizeConverterBuilder addGB(double gb) {
			addMB((long)(gb * MULTIPLIER));
			return this;
		}

		@Override
		public SizeConverterBuilder addTB(double tb) {
			addGB((long)(tb * MULTIPLIER));
			return this;
		}

		@Override
		public long toBytes() {
			return bytes;
		}

		@Override
		public double toKB() {
			return toBytes() / (double)MULTIPLIER;
		}

		@Override
		public double toMB() {
			return toKB() / (double)MULTIPLIER;
		}

		@Override
		public double toGB() {
			return toMB() / (double)MULTIPLIER;
		}

		@Override
		public double toTB() {
			return toGB() / (double)MULTIPLIER;
		}
		
		private String getSufixo(int potencia) {
			switch(potencia) {
				case 0: return "BT";
				case 1: return "KB";
				case 2: return "MB";
				case 3: return "GB";
				case 4: return "TB";
				default: throw new RuntimeException("Não foi definido um sufixo para a potência " + potencia);
			}
		}
		
		@Override
		public String toString() {
			double valor = bytes;
			int potencia = 0;
			while(valor >= 1000) {
				valor = valor / MULTIPLIER;
				if(++potencia == 4) {
					break;
				}
			}
			String sufixo = getSufixo(potencia);
			String texto = FORMATTER.format(valor);
			if(sufixo != null) {
				texto += " " + sufixo;
			}
			return texto;
		}
	}
}
