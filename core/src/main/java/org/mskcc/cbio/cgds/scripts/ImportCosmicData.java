/** Copyright (c) 2012 Memorial Sloan-Kettering Cancer Center.
**
** This library is free software; you can redistribute it and/or modify it
** under the terms of the GNU Lesser General Public License as published
** by the Free Software Foundation; either version 2.1 of the License, or
** any later version.
**
** This library is distributed in the hope that it will be useful, but
** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
** documentation provided hereunder is on an "as is" basis, and
** Memorial Sloan-Kettering Cancer Center 
** has no obligations to provide maintenance, support,
** updates, enhancements or modifications.  In no event shall
** Memorial Sloan-Kettering Cancer Center
** be liable to any party for direct, indirect, special,
** incidental or consequential damages, including lost profits, arising
** out of the use of this software and its documentation, even if
** Memorial Sloan-Kettering Cancer Center 
** has been advised of the possibility of such damage.  See
** the GNU Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public License
** along with this library; if not, write to the Free Software Foundation,
** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
**/

package org.mskcc.cbio.cgds.scripts;

import org.mskcc.cbio.cgds.dao.DaoException;
import org.mskcc.cbio.cgds.dao.DaoCosmicData;
import org.mskcc.cbio.cgds.dao.MySQLbulkLoader;
import org.mskcc.cbio.cgds.util.ConsoleUtil;
import org.mskcc.cbio.cgds.util.FileUtil;
import org.mskcc.cbio.cgds.util.ProgressMonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mskcc.cbio.cgds.dao.DaoGeneOptimized;
import org.mskcc.cbio.cgds.model.CanonicalGene;
import org.mskcc.cbio.cgds.model.CosmicMutationFrequency;
import org.mskcc.cbio.cgds.util.MutationKeywordUtils;

/**
 * Command Line Tool to Import Background Gene Data.
 */
public class ImportCosmicData {
    private ProgressMonitor pMonitor;
    private File file;

    public ImportCosmicData(File file, ProgressMonitor pMonitor) {
        this.file = file;
        this.pMonitor = pMonitor;
    }

    public void importData() throws IOException, DaoException {
        DaoGeneOptimized daoGeneOptimized = DaoGeneOptimized.getInstance();
        Pattern p = Pattern.compile("GENE=([^;]+);STRAND=(.);CDS=([^;]+);AA=p\\.([^;]+);CNT=([0-9]+)");
        MySQLbulkLoader.bulkLoadOn();
        FileReader reader = new FileReader(file);
        BufferedReader buf = new BufferedReader(reader);
        String line;
        while ((line = buf.readLine()) != null) {
            if (pMonitor != null) {
                pMonitor.incrementCurValue();
                ConsoleUtil.showProgress(pMonitor);
            }
            if (!line.startsWith("#")) {
                String parts[] = line.split("\t",-1);
                if (parts.length<8) {
                    System.err.println("Wrong line in cosmic: "+line);
                    continue;
                }
                
                String id = parts[2];
                Matcher m = p.matcher(parts[7]);
                if (m.find()) {
                    String gene = m.group(1);
                    CanonicalGene canonicalGene = daoGeneOptimized.getNonAmbiguousGene(gene);
                    if (canonicalGene==null) {
                        System.err.println("Gene symbol in COSMIC not recognized: "+gene);
                        continue;
                    }
                    
                    String aa = m.group(4);
                    String keyword = MutationKeywordUtils.guessCosmicKeyword(aa);
                    if (keyword == null) {
                        continue;
                    }
                    
                    int count = Integer.parseInt(m.group(5));
                
                    CosmicMutationFrequency cmf = new CosmicMutationFrequency(id, 
                            canonicalGene.getEntrezGeneId(), aa, gene + " " + keyword, count);
                    
                    cmf.setChr(parts[1]);
                    cmf.setStartPosition(Long.parseLong(parts[1]));
                    cmf.setReferenceAllele(parts[3]);
                    cmf.setTumorSeqAllele(parts[4]);
                    cmf.setStrand(m.group(2));
                    cmf.setCds(m.group(3));
                
                    DaoCosmicData.addCosmic(cmf);
                }
            }
        }
        reader.close();
        if (MySQLbulkLoader.isBulkLoad()) {
           MySQLbulkLoader.flushAll();
        }        
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("command line usage:  importCosmicData.pl <CosmicCodingMuts.vcf>");
            System.exit(1);
        }
        DaoCosmicData.deleteAllRecords();
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);

        File file = new File(args[0]);
        System.out.println("Reading data from:  " + file.getAbsolutePath());
        int numLines = FileUtil.getNumLines(file);
        System.out.println(" --> total number of lines:  " + numLines);
        pMonitor.setMaxValue(numLines);
        ImportCosmicData parser = new ImportCosmicData(file, pMonitor);
        parser.importData();
        ConsoleUtil.showWarnings(pMonitor);
        System.err.println("Done.");
    }
}