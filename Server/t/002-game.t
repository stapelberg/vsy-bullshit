#!perl
# vim:ts=4:sw=4:expandtab

use strict;
use warnings;
use Test::More;
use Game;
use Games;
use Player;

my $p = Player->new(nickname => 'sECuRE');
isa_ok($p, 'Player');

my $g = Game->new(size => 3);
isa_ok($g, 'Game');

my $games = Games->instance;

is(scalar $games->games, 0, 'no games in global list yet');

$games->add_game($g);

is(scalar $games->games, 1, 'one game in global list');

$g->add_player($p);

is($g->winner, undef, 'no winner yet');

# diagonal from (left, top) to (right, bottom)
$g->make_move($p, 0);
$g->make_move($p, 4);
$g->make_move($p, 8);

is($g->winner, $p, 'i have won');

# add another game to make sure the del_player does not delete all games
my $othergame = Game->new(size => 3);
$games->add_game($othergame);
is(scalar $games->games, 2, 'other game added');

$g->del_player($p);

my @existing = map { $_->id } $games->games;
my $expected = [ $othergame->id ];
is_deeply(\@existing, $expected, 'game removed after winner has left');

done_testing;
