#!perl
# vim:ts=4:sw=4:expandtab

use strict;
use warnings;
use Test::More;
use Game;
use Player;

my $p = Player->new(nickname => 'sECuRE');
isa_ok($p, 'Player');

my $g = Game->new(size => 3);
isa_ok($g, 'Game');

$g->add_player($p);

is($g->winner, undef, 'no winner yet');

# diagonal from (left, top) to (right, bottom)
$g->make_move($p, 0);
$g->make_move($p, 4);
$g->make_move($p, 8);

is($g->winner, $p, 'i have won');

done_testing;
