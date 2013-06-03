package output;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.io.FileInfo;
import ij.*;
import ij.gui.*;
import ij.plugin.frame.PlugInFrame;
import ij.process.*;
import java.awt.image.*;
import java.awt.Color;
import ij.process.ImageConverter;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class OutputHistogramEqualizationFilter implements PlugInFilter {

	FileInfo fi;
	ImagePlus img;
	int file;

	public int setup (String arg, ImagePlus img) {

		if(img==null)
		{
			IJ.showMessage("Equalizza Plugin","Caricare prima un'immagine");
			return DONE;
		}
		fi=img.getOriginalFileInfo();
		this.img=img;
		file=img.getType();
		return DOES_ALL;
	}

	@SuppressWarnings("unused")
	public void run(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		int M = w * h;
		int stackSize=img.getStackSize();	
		String choice="Intensità";
		if (stackSize>1){							//Stack di immagini
			IJ.showMessage("Equalizza Plugin","Il plugin non supporta gli stack di immagini");
			return;
		}
		if ((file == 0) || (file ==1)) {					//GRAY8 OR GRAY16
			int[] H = ip.getHistogram();					//Istogramma
			Cumulativo (H);							//Itogramma cumulativo
			ScriviGray(H,h,w,M,ip);						//Scrittura dei nuovi valori
		}	
			
		else if (file==4){                                      			//RGB

			
			
			
			/*GenericDialog gd = new GenericDialog ( "Scegli");
			String scelta [] = {"Intensità","Colore"};
			gd.addChoice("Metodo: ", scelta, scelta[0]);
			gd.showDialog();
			choice = gd.getNextChoice();
			if (gd.wasCanceled()) return;*/
			
			if (/*choice=="Intensità"*/ false){				
				int [] Cb= new int[M];
				int [] Cr= new int[M];
				int [] y = new int[M];
				byte []rr = new byte [M];
				byte []gg= new byte [M];
				byte []bb = new byte [M];
				ColorProcessor clp = (ColorProcessor) ip;
				clp.getRGB(rr,gg,bb);
				RgbToYcbcr(rr,gg,bb,y,Cb,Cr);			//Calcolo y,cb,cr
				int [] liv = new int [256];
				int [] L = new int[M];
				int [] N = new int[M];
				Istogramma (y, liv);  					//Istogramma della luminanza
				Cumulativo (liv);    					//Istogramma cumulativo della luminanza
				Equalizza (y,liv,M,L);    				//Equalizzazione dell'istogramma
				ycbcrToRgb (L, Cb, Cr,N);    				//Conversione da YCbCr a RGB
				ScriviRGB(N,h,w,ip);					//Inserimento dei valori nell'immagine
			}				
		
			
			else if (/*choice=="Colore"*/ true){				
				int R [] = new int [M];
				int G [] = new int [M];
				int B [] = new int [M];
				int HR[] = new int [256];
				int HG[] = new int [256];
				int HB[] = new int [256];
				int ER[] = new int [M];
				int EG[] = new int [M];	
				int EB[] = new int [M];
				int HI[] = new int [M];
				int[] pix = (int[])ip.getPixels();
				for (int i=0;i<pix.length;i++){			//Estraggo r,g,b
					R[i] = (pix[i] & 0x00ff0000)>>16;
					G[i] = (pix[i] & 0x0000ff00)>>8;
					B[i] = pix[i] & 0x000000ff;
				}
				Istogramma(R,HR);					//Istogramma del Rosso
				Istogramma(G,HG);					//Istogramma del Verde
				Istogramma(B,HB);					//Istogramma del Blu
				Cumulativo (HR);						//Istogramma cumulativo del Rosso
				Cumulativo (HG);						//Istogramma cumulativo del Verde
				Cumulativo (HB);						//Istogramma cumulativo del Blu
				Equalizza(R,HR,M,ER);					//Equalizzazione dell'istogramma del Rosso
				Equalizza(G,HG,M,EG);					//Equalizzazione dell'istogramma del Verde
				Equalizza(B,HB,M,EB);					//Equalizzazione dell'istogramma del Blu
				Unisci(ER,EG,EB,HI);					//Unione del Rosso, del Verde e del Blu
				ScriviRGB(HI,h,w,ip);					//Scrittura dei nuovi valori
			}
		}

	
		else if (file==3){							//PALETTE
			IndexColorModel icm = (IndexColorModel) ip.getColorModel();
			byte [] pix = (byte[])ip.getPixels();
			int pixBits = icm.getPixelSize();
			int mapsize = icm.getMapSize();
			byte [] red= new byte[mapsize];
			byte [] green= new byte[mapsize];
			byte [] blue= new byte[mapsize];
			icm.getReds(red);
			icm.getGreens(green);
			icm.getBlues(blue);
			GenericDialog gd = new GenericDialog ( "Scegli");
			String scelta [] = {"Intensità","Colore"};
			gd.addChoice("Metodo: ", scelta, scelta[0]);
			gd.showDialog();
			choice = gd.getNextChoice();
			if (gd.wasCanceled()) return;
			
			if (choice=="Intensità"){				
				int [] y = new int [mapsize];
				int [] Cb = new int [mapsize];
				int [] Cr = new int [mapsize];
				RgbToYcbcr(red,green,blue,y,Cb,Cr);
				int [] liv = new int [256];
				int [] L = new int [256];
				IstogrammaPal(y,pix,liv);				//Istogramma della luminanza
				Cumulativo (liv);						//Istogramma cumulativo della luminanza
				Equalizza (y,liv,M,L);					//Equalizzazione dell'istogramma
				YCbCrToRGB(L,Cb,Cr,red,green,blue);		//Conversione da YCbCr a RGB
				IndexColorModel icm2 = new IndexColorModel(pixBits, mapsize, red, green, blue);
				ip.setColorModel(icm2);					//Scrittura dei nuovi valori
				WindowManager.getCurrentImage().updateAndDraw();
			}

			else if (choice=="Colore"){					
				int HRed[]=new int [256];
				int HGreen[]=new int [256];
				int HBlue[] = new int [256];
				byte ERed[] = new byte [256];
				byte EGreen[] = new byte [256];	
				byte EBlue[] = new byte [256];
				IstogrammaPalette(red,pix,HRed);			//Istogramma del Rosso
				IstogrammaPalette(green,pix,HGreen);		//Istogramma del Verde
				IstogrammaPalette(blue,pix,HBlue);			//Istogramma del Blu
				Cumulativo (HRed);					//Istogramma cumulativo del Rosso
				Cumulativo (HGreen);					//Istogramma cumulativo del Verde
				Cumulativo (HBlue);					//Istogramma cumulativo del Blu
				EqualizzaPalette(HRed,red,M,ERed);		//Equalizzazione dell'istogramma del Rosso
				EqualizzaPalette(HGreen,green,M,EGreen);	//Equalizzazione dell'istogramma del Verde
				EqualizzaPalette(HBlue,blue,M,EBlue);		//Equalizzazione dell'istogramma del Blu
				IndexColorModel icm2 = new IndexColorModel(pixBits, mapsize, ERed, EGreen, EBlue);
				ip.setColorModel(icm2);					//Scrittura dei nuovi valori
				WindowManager.getCurrentImage().updateAndDraw();
			}
		}


		else if (file==2){							//GRAY32
			
			GenericDialog gd = new GenericDialog ( "Scegli");
			String scelta [] = {"ConvertTo16bit","LowEqualization","HighEqualization"};
			gd.addChoice("Metodo: ", scelta, scelta[0]);
			gd.showDialog();
			choice = gd.getNextChoice();
			if (gd.wasCanceled()) return;

			if(choice=="ConvertTo16bit"){					
				ImageConverter iConv = new ImageConverter(img);
				iConv.convertToGray16();
				ip=img.getProcessor();
				int[] H = ip.getHistogram();				//Istogramma
				Cumulativo (H);						//Istogramma Cumulativo
				ScriviGray(H,h,w,M,ip);					//Settaggio dei nuovi valori
			}

			else if ((choice=="LowEqualization") || (choice=="HighEqualization")){	
				int H [] = new int [65536];
				float b=(float)(0.5/65535);				//Range di scalatura (valori float compresi tra 0.0 e 1.0)
				Binning(H,h,w,b,choice,ip);				//BINNING HISTOGRAM
				ImagePlus nn = NewImage.createShortImage("Equalized Image",w,h,1,NewImage.FILL_BLACK);
				ImageProcessor nn_ip = nn.getProcessor();
				Cumulativo(H);						//Istogramma cumulativo
				Scrivi32(H,M,h,w,b,choice,ip,nn_ip);		//Scrittura dei nuovi valori
				nn.show();
				nn.updateAndDraw();
			}
		}	
	
		String f=fi.directory+fi.fileName;
		String out = f+".html";
		try{
		FileWriter fw = new FileWriter(out);				//Report di ImageJ in formato html salvato automaticamente nella directory del file
		fw.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">"+
				    "<head><meta http-equiv=\"ContentType\"content=\"text/html; charset=utf-8\"/></head> <body> <h1>ImageJ Report</h1> "+
				    "Report generation: "+getDateTime()+"<br>User: "+System.getProperty("user.name")+"<br>Workstation: "+System.getenv("COMPUTERNAME")+ 
				    "<br><br><h2> Riepilogo:</h2> <h3> Catena1 : <a href=\"#chain0\"> Carica Immagine</a></h3><br>"+
				    "1.<a href=\"#ring0\">Carica Immagine </a><span style=\"font-style: italic;\">&nbsp;Carica un'immagine da file. </span><br>"+
				    "2.<a href=\"#ring1\">Equalizzazione Istogramma </a><span style=\"font-style:italic;\">&nbsp;"+
				    "Migliora il contrasto dell'immagine equalizzando l'istogramma dei suoi valori di intensità.</span>"+
				    "<br><p></p><hr><h2>Dettagli:</h2><h3> Catena1: <a name=\"chain0\">Carica Immagine</a></h3><h4><a name=\"ring0\">"+
				    "1.Carica Immagine</a><br><span style=\"font-style: italic;font-weight:normal\">Carica un'immagine da file.</span><br></h4>"+
				    "<strong>Dettagli:</strong><br><br>Il plugin supporta immagini in scala di grigio a 8,16 e 32 bit e immagini a colori a 8 e 32 bit<br><br>"+
				    "<strong>Parametri:</strong><br><br><span style=\"font-weight: bold;\">Percorso: </span>"+f+"</h4><br><br><h4><a name=\"ring1\">"+
				    "2.Equalizzazione Istogramma</a><br><span style=\"font-style: italic;font-weight:normal\">"+
				    "Migliora il contrasto dell'immagine equalizzando l'istogramma dei suoi valori di intensità. </span><br></h4><strong>Dettagli:</strong><br><br>"+
				    "\"Equalizzazione Istogramma\" regola i valori dei pixel dell'immagine distribuendoli in maniera più uniforme su tutto l'intervallo disponibile.<br>"+
				    "E\' possibile equalizzare l'istogramma delle immagini RGB e a scala di colore utilizzando una delle seguenti modalità:<br>"+
				    "-\"Modalità Intensità\": l'equalizzazione viene applicata alla luminanza totale, utilizzando lo spazio di colore YCbCr;<br>"+
				    "-\"Modalità Colori\": l'equalizzazione viene applicata separatamente sui tre canali di colore RGB.<br>"+
				    "Per le immagini in scala di grigio a 8 e 16 bit le due modalità sopra elencate forniscono gli stessi risultati.<br>"+
				    "Per le immagini in scala di grigio a 32 bit è possibile scegliere tra :<br>"+
				    "-\"ConvertTo16bit\": converte l'immagine in una a scala di grigio a 16 bit e ne equalizza l'istogramma. <br>"+
			            "-\"LowEqualization\": equalizza l'istogramma binning dei pixel con valore inferiore ad una determinata soglia.<br>"+
				    "-\"HighEqualization\": equalizza l'istogramma binning dei pixel con valore superiore ad una determinata soglia.<br><br>"+
				    "<strong>Parametri:</strong><br><br><span style=\"font-weight: bold;\">Modalità: </span>"+choice+
				    "<br><span style=\"font-style: italic;\">Tipo di regolazione da applicare.</span><br><br></body></html>");
			fw.close();
		}
		catch (Exception e){
		System.exit(1);}
	}

		//METODI
		//Cumulativo somma il valore di ogni indice dell'istogramma con il valore dell'indice precendente
		private void Cumulativo ( int [] Hist){
			for (int j = 1; j < Hist.length; j++) 
					Hist[j] = Hist[j - 1] + Hist[j];
		}

		//Istogramma incrementa il valore dell'indice dell'istogramma corrispondente al valore dell'intensità del pixel
		private void Istogramma (int [] l, int [] m){
				for (int c=0;c<l.length;c++)
					m[l[c]]=m[l[c]]+1;
		}

		//IstogrammaPal incrementa il valore dell'indice dell'istogramma corrispondente al valore dell'intensità del pixel
		private void IstogrammaPal (int [] yy, byte [] pixx, int [] livv){
				for (int c=0; c<pixx.length;c++)
					livv[yy[pixx[c]&0xff]]=livv[yy[pixx[c]&0xff]]+1;
		}

		//IstogrammaPalette incrementa il valore dell'indice dell'istogramma corrispondente al valore dell'indice della palette colore relativo al pixel
		private void IstogrammaPalette (byte [] col, byte [] pix, int [] livv){
					for (int c=0; c< pix.length; c++)
						livv[col[pix[c]&0xff]&0xff]=livv[col[pix[c]&0xff]&0xff]+1;
		}

		//Equalizza moltiplica il valore dell'indice dell'istogramma cumulativo relativo alla luminanza del pixel per (tutti i livelli-1)/tutti i pixel
		private void Equalizza (int [] yy, int [] livv, int p, int [] LL){
				for (int z=0; z<yy.length; z++)
					LL[z]=(int)Math.floor(livv[yy[z]]*(livv.length - 1) / p);

		}

		//EqualizzaPalette moltiplica il valore dell'indice dell'istogramma cumulativo relativo al colore del pixel per (tutti i livelli-1)/tutti i pixel
		private void EqualizzaPalette (int [] ll, byte [] colore, int p, byte [] L){
				for (int z=0; z<colore.length; z++){
					int col=(int)Math.floor(ll[(colore[z]&0xff)]*(ll.length - 1) / p);
					L[z]=(byte)col;
				}
		}

		//ycbcbToRgb restituisce un singolo array dato dall'unione dei tre colori ottenuti con i valori normalizzati della luminanza
		private void ycbcrToRgb (int [] LL, int [] Cb, int [] Cr, int [] NN){
				for (int m=0;m<LL.length;m++){
					int r = (int)Math.round(LL[m] + 1.402 * Cr[m] - 0.701);
					int g = (int)Math.round(LL[m] - 0.714 * Cr[m] - 0.344 * Cb[m] + 0.529);
					int b = (int)Math.round(LL[m] + 1.772 * Cb[m] - 0.886);
					r=Controlla (r);
					g=Controlla (g);
					b=Controlla (b);
					NN[m] = ((r & 0xff)<<16)+((g & 0xff)<<8)+(b & 0xff);
				}
		}
		
		//YCbCrToRGB restituisce i tre array dei colori ricostruiti con i valori normalizzati della luminanza 
		private void YCbCrToRGB(int [] LL, int [] Cb, int [] Cr, byte [] red, byte []green, byte [] blue){
		for (int m=0;m<LL.length;m++){
					int r = (int)Math.round(LL[m] + 1.402 * Cr[m] - 0.701);
					int g = (int)(LL[m] - 0.714 * Cr[m] - 0.344 * Cb[m] + 0.529);
					int b = (int)Math.round(LL[m] + 1.772 * Cb[m] - 0.886);
					r=Controlla(r);
					g=Controlla(g);
					b=Controlla(b);
					red[m]=(byte)r;
					green[m]=(byte)g;
					blue[m]=(byte)b;
				}
		}
		
		//RgbToYcbcr converte r,g,b in int e calcola y,Cb,Cr
		private void RgbToYcbcr (byte [] red, byte [] green,byte []blue, int [] y, int [] Cb, int [] Cr){
				for ( int i=0;i<y.length;i++){			
					int r=red[i]&0xff;
					int g=green[i]&0xff;
					int b=blue[i]&0xff;
					y[i] = (int)Math.round(0.299 * r + 0.587 * g + 0.114 * b);
					Cb[i]= (int)Math.round(-0.16874 * r - 0.33126 * g + 0.50000 * b);
					Cr[i]= (int)Math.round(0.50000 * r - 0.41869 * g - 0.08131 * b);
				}
		}

		//controlla setta eventualmente il valore del colore ad un valore limite 
		private int Controlla (int value){
				if (value < 0)
					value=0;
				if (value > 255)
					value=255;
			return value;
		}
		
		//Unisci unifica i valori dei tre colori in un unico valore
		private void Unisci (int [] RR, int [] GG, int [] BB, int [] HH){
				for (int k=0; k<RR.length; k++)
					HH[k] = ((RR[k] & 0xff)<<16)+((GG[k] & 0xff)<<8)+(BB[k] & 0xff);
		}
		
		//ScriviGray16 setta i nuovi valori dei pixel nell'immagine dopo averli equalizzati
		private void ScriviGray (int [] HH, int hh, int ww, int MM, ImageProcessor ipp){
			double vol=(double)MM;
			double scale=(HH.length-1)/(vol);
			for (int v = 0; v < hh; v++) {
				for (int u = 0; u < ww; u++) {
					int a=ipp.get(u,v);
					int b = (int)Math.floor(HH[a]*scale);
					ipp.set(u, v, b);
				}
			}
		}

		//ScriviRGB setta i nuovi valori dei pixel nell'immagine dopo averli equalizzati
		private void ScriviRGB (int [] LL, int hh, int ww, ImageProcessor ipp){
			int indice=0;          				 
				for (int v = 0; v < hh; v++) {
					for (int u = 0; u < ww; u++) {
						ipp.set(u,v,LL[indice]);
						indice++;
					}
				}
		}
		
		//Binning scala i valori dei pixel minori di 0.5 nel caso di LowHistogram o maggiori di 0.5 nel caso di HighHistogram
		//in un intervallo compreso tra 0 e 65535 e ne crea l'istogramma.
		private void Binning (int [] HH, int hh , int ww, float bb, String choicee,ImageProcessor ipp){
			for (int v = 0; v < hh; v++) {									
					for (int u = 0; u < ww; u++) {
						float a = ipp.getPixelValue(u, v);
						if ((a<0.5)&&(choicee=="LowEqualization")){
							int i= (int)Math.floor(a / bb);
							HH[i]=HH[i]+1;
						}
						else if ((a>=0.5)&&(choicee=="HighEqualization")){
							int i= (int)Math.floor(a / bb)-65535;
							HH[i]=HH[i]+1;
						}
					}	
				}
		}
		
		//Scrivi32 per ogni pixel setta il valore equalizzato del pixel se minore di 0.5 oppure 65535 se maggiore o uguale di 0.5 nel caso di scelta di LowHistogram
		//nel caso di scelta di HighHistogram setta invece il valore equalizzato del pixel se maggiore o uguale di 0.5 oppure 0 se minore di 0.5.
		private void Scrivi32 (int [] HH, int MM,int hh, int ww, float bb, String choicee, ImageProcessor ip, ImageProcessor ipp){
			double vol = (double)MM;
			double scale=(HH.length-1)/(vol);
			for (int s = 0; s < hh; s++) {						
					for (int t = 0; t < ww; t++) {
						float val = ip.getPixelValue(t, s);
						if ((val<0.5)&&(choicee=="LowEqualization")){
							int ind= ((int)Math.floor(val / bb));
							int n = (int)Math.floor(HH[ind]*scale);
							ipp.set(t,s,n);
						}
						else if((val>=0.5)&&(choicee=="LowEqualization"))
							ipp.set(t,s,65535);
						else if ((val>=0.5)&&(choicee=="HighEqualization")){
							int ind= ((int)Math.floor(val / bb))-65535;
							int n = (int)Math.floor(HH[ind]*scale);
							ipp.set(t,s,n);
						}
						else if ((val<0.5)&&(choicee=="HighEqualization"))
							ipp.set(t,s,0);
					}	
			}
		}
		
		//restituisce la data e l'ora corrente nel formato indicato
		private String getDateTime() {
        		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        		Date date = new Date();
        		return dateFormat.format(date);
    		}				
}