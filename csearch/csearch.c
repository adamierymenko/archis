/* Optimum code search for the Archis project */
/* (c)2002 Adam Ierymenko, All Rights Reserved */

/* This program uses a simple "hill-climbing" type search to find the
   most evolvable mapping of instructions to 6-bit codons.  The results
   of this program were used in the design of the Genome Virtual Machine
   for the Archis alife simulator.  This was inspired by the following
   article:
   
   Freeland S. J. and Hurst L. D.  1998.  The Genetic Code is One in
   a Million.  J. Mol. Evol.  47:238-248.  */

/* Should compile with a simple cc or gcc command on any relatively
   normal Unix system, MacOSX, or on Windows using Cygwin.
   
   Example (Linux): gcc -O -s -o csearch csearch.c */

#include <stdio.h>
#include <stdarg.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <time.h>

/* Some defines that specify various constants in the system */
#define NUM_CODES 64
#define NUM_CODONS 21
#define CODE_LENGTH 6
#define DISPLAY_INTERVAL 50000

/* Maximum number of trials without an improvment before a new
   run is undertaken. */
#define RUN_RESTART_THRESHOLD 10000000

/* Structure for codon info */
/* a = accumulator effect axis
 * p = pointer effect axis
 * f = flow effect axis */
struct codon_info {
	float a,p,f;
	int max,min;
};

/* Structure for a code set */
struct code_set {
	int codes[NUM_CODES];
	int code_mappings[NUM_CODES];
};

/* Codes for codons */
char *codes[] = {
	"000000",
	"000001",
	"000010",
	"000011",
	"000100",
	"000101",
	"000110",
	"000111",
	"001000",
	"001001",
	"001010",
	"001011",
	"001100",
	"001101",
	"001110",
	"001111",
	"010000",
	"010001",
	"010010",
	"010011",
	"010100",
	"010101",
	"010110",
	"010111",
	"011000",
	"011001",
	"011010",
	"011011",
	"011100",
	"011101",
	"011110",
	"011111",
	"100000",
	"100001",
	"100010",
	"100011",
	"100100",
	"100101",
	"100110",
	"100111",
	"101000",
	"101001",
	"101010",
	"101011",
	"101100",
	"101101",
	"101110",
	"101111",
	"110000",
	"110001",
	"110010",
	"110011",
	"110100",
	"110101",
	"110110",
	"110111",
	"111000",
	"111001",
	"111010",
	"111011",
	"111100",
	"111101",
	"111110",
	"111111" };

/* Codons */
char *codons[] = {
	"fwd",
	"back",
	"go",
	"add",
	"mul",
	"div",
	"and",
	"or",
	"xor",
	"shl",
	"shr",
	"inc",
	"dec",
	"sta",
	"cmp",
	"loop",
	"rep",
	"read",
	"write",
	"sch",
	"stop" };

/* Codon information structures */
struct codon_info cinfo[21];

/* Codons padded for display */
char *codons_padded[] = {
	"fwd    ",
	"back   ",
	"go     ",
	"add    ",
	"mul    ",
	"div    ",
	"and    ",
	"or     ",
	"xor    ",
	"shl    ",
	"shr    ",
	"inc    ",
	"dec    ",
	"sta    ",
	"cmp    ",
	"loop   ",
	"rep    ",
	"read   ",
	"write  ",
	"sch    ",
	"stop   " };

/* Initializes the cinfo[] array */
void init_cinfo()
{
	int x = 0;
	
	/* fwd */
	cinfo[x].a = 0.0;
	cinfo[x].p = 1.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 6;
	cinfo[x].min = 4;
	++x;
	
	/* back */
	cinfo[x].a = 0.0;
	cinfo[x].p = 1.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 6;
	cinfo[x].min = 4;
	++x;
	
	/* go */
	cinfo[x].a = 0.0;
	cinfo[x].p = 2.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* add */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* mul */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* div */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* and */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* or */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* xor */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* shl */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* shr */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* inc */
	cinfo[x].a = 1.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* dec */
	cinfo[x].a = 1.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* sta */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* cmp */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* loop */
	cinfo[x].a = 0.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 1.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* rep */
	cinfo[x].a = 0.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 1.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* read */
	cinfo[x].a = 2.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* write */
	cinfo[x].a = 0.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* sch */
	cinfo[x].a = 0.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 0.0;
	cinfo[x].max = 4;
	cinfo[x].min = 2;
	++x;
	
	/* stop */
	cinfo[x].a = 0.0;
	cinfo[x].p = 0.0;
	cinfo[x].f = 4.0;
	cinfo[x].max = 3;
	cinfo[x].min = 2;
}

/* Prints out a code set */
void print_code_set(struct code_set *cs,char *prefix)
{
	register int i;
	for(i=0;i<16;i++) {
		printf("%s%s 0%x %s  %s %x %s  %s %x %s  %s %x %s\n",prefix,
			codes[cs->codes[i]],strtol(codes[cs->codes[i]],(char **)0,2),codons_padded[cs->code_mappings[i]],
			codes[cs->codes[i+16]],strtol(codes[cs->codes[i+16]],(char **)0,2),codons_padded[cs->code_mappings[i+16]],
			codes[cs->codes[i+32]],strtol(codes[cs->codes[i+32]],(char **)0,2),codons_padded[cs->code_mappings[i+32]],
			codes[cs->codes[i+48]],strtol(codes[cs->codes[i+48]],(char **)0,2),codons_padded[cs->code_mappings[i+48]]);
	}
	fflush(stdout);
}

int main(int argc,char **argv)
{
	/* Counter and temp variables used in various places */
	char newcode[CODE_LENGTH+1];
	int counts[NUM_CODONS];
	register int i,j;
	int count_exceeded = 0;
	int newrun = 0;
	int k,l;

	/* Variables used in main loop */
	register float n,distance;
	float current_run_score;
	
	/* Trial number */
	unsigned int trial = 0;
	
	/* Run number */
	unsigned int run = 0;
	
	/* Trial when last improvement was made */
	unsigned int trial_last_improvement = 0;
	
	/* Current code */
	struct code_set current;
	
	/* Best code out of all sets */
	struct code_set best;
	float best_score = 999999.0;
	
	/* Best code out of current run */
	struct code_set best_current_run;
	float best_current_run_score = 999999.0;

	/* Matrix of potential mutations */
	int mutations[NUM_CODES][CODE_LENGTH];

	/* Seed random number generator */
	srand(((unsigned int)time(NULL)) * (unsigned int)2);
	
	/* Initialize cinfo[] array */
	init_cinfo();
	
	/* Create a matrix of potential single point mutations */
	for(i=0;i<NUM_CODES;i++) {
		strcpy(newcode,codes[i]);
		for(j=0;j<CODE_LENGTH;j++) {
			/* Flip bit j */
			if (newcode[j] == '0')
				newcode[j] = '1';
			else newcode[j] = '0';
			
			/* Search for index in code list that mutation matches */
			for(k=0;k<NUM_CODES;k++) {
				if (!strcmp(newcode,codes[k])) {
					mutations[i][j] = k;
					break;
				}
			}
			
			/* Flip bit j back */
			if (newcode[j] == '0')
				newcode[j] = '1';
			else newcode[j] = '0';
		}
	}

	/* Main loop */
	for(;;) {
		if (((trial - trial_last_improvement) > RUN_RESTART_THRESHOLD)||(trial == 0)) {
			/* If this is the start of a run, begin with a random code set */
			for(i=0;i<NUM_CODES;i++) {
				current.codes[i] = i;
				current.code_mappings[i] = rand() % NUM_CODONS;
			}
			best_current_run_score = 999999.0;
			newrun = 1;
		} else {
			/* Otherwise, perturb the current best code set of run */
			memcpy(&current,&best_current_run,sizeof(struct code_set));
			j = (rand() % 6)+1;
			for(i=0;i<j;i++)
				current.code_mappings[rand() % NUM_CODES] = rand() % NUM_CODONS;
		}
		
		/* Check to make sure counts for each codon to not exceed specified
		   threshold values. */
		count_exceeded = 0;
		for(i=0;i<NUM_CODONS;i++)
			counts[i] = 0;
		for(i=0;i<NUM_CODES;i++) {
			if (++counts[current.code_mappings[i]] > cinfo[current.code_mappings[i]].max) {
				count_exceeded = 1;
				break;
			}
		}
		if (!count_exceeded) {
			for(i=0;i<NUM_CODONS;i++) {
				if (counts[i] < cinfo[i].min) {
					count_exceeded = 1;
					break;
				}
			}
		}

		if (!count_exceeded) {
			/* Increment run counter if we made it this far and it's a new run */
			if (newrun) {
				newrun = 0;
				if (trial > 0)
					++run;
			}
			
			/* Here we compute the distance from one codon to
			 * another for all it's mutations. */
			current_run_score = 0.0;
			for(i=0;i<NUM_CODES;i++) {
				distance = 0.0;
				k = current.code_mappings[i];
				for(j=0;j<CODE_LENGTH;j++) {
					l = current.code_mappings[mutations[i][j]];
					/* Penalty if mutation results in a different codon */
					if (k != l)
						distance += 2.0;
					n = (cinfo[k].p - cinfo[l].p);
					distance += n * n;
					n = (cinfo[k].a - cinfo[l].a);
					distance += n * n;
					n = (cinfo[k].f - cinfo[l].f);
					distance += n * n;
				}
				current_run_score += distance;
			}
			current_run_score /= 64.0;
						
			/* Look for best scores if our improvement exceeds a certain
			   threshold value intended to compensate for FPU wierness or
			   minor insignificant improvements. */
			if ((best_score - current_run_score) > 0.0000001) {
				memcpy(&best,&current,sizeof(struct code_set));
				best_score = current_run_score;
			}
			if ((best_current_run_score - current_run_score) > 0.0000001) {
				memcpy(&best_current_run,&current,sizeof(struct code_set));
				best_current_run_score = current_run_score;
				trial_last_improvement = trial;
			}

			/* Display status periodically */
			if ((trial % DISPLAY_INTERVAL) == 0) {
				printf("*** Trial: %u, Run: %u ***\n\n",trial,run);
				printf("Best score for current run: %13.18f\n",best_current_run_score);
				print_code_set(&best_current_run,"  ");
				printf("(Trials since last improvement: %u)\n",(trial - trial_last_improvement));
				printf("\nBest score for all runs: %13.18f\n",best_score);
				print_code_set(&best,"  ");
			}
			
			++trial;
		}
	}
}
