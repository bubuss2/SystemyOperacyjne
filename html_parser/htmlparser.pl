#!/usr/bin/perl

$filename = $ARGV[0];
open(FH,'<', $filename) or die $!;
while (<FH>){
    $line = $_;
    
    if ($line =~ /(?i)<[A][^>]*?[H][R][E][F]=\"([^>]*?)\"[^>]*?>\s*([\w\W]*?)\s*<\/[A]>(?-i)/){
        print "$1 \n";
    }
}
close(FH)
    