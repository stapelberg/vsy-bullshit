# vim:ts=4:sw=4:expandtab
package PlayerState;

use strict;
use Moose;
use Player;
use Digest::SHA1 qw(sha1_hex);
use v5.10;

=head1 NAME

PlayerState - bundles a C<Player> and game state

=head1 SYNOPSIS

 use PlayerState;
 use Player;
 
 my $player = Player->new(nickname => 'sECuRE');
 my $state = PlayerState->new(player => $player);
 
 $state->save_move(3);
 $state->save_move(4);
 $state->save_move(5);
 
 say "Winner" if $state->has_fields([ 3, 4, 5 ]); # prints "Winner"

=head1 ATTRIBUTES

=head2 player([$player])

Reference to the C<Player> object.

=cut
has 'player' => (is => 'ro', isa => 'Ref', required => 1);
has 'moves' => (
    traits => [ 'Array' ],
    is => 'ro',
    isa => 'ArrayRef[Int]',
    default => sub { [] },
    handles => {
        save_move => 'push',
    }
);

=head1 METHODS

=head2 has_fields($fields)

Checks if the fields C<$fields> (an ArrayRef) are in the player's moves.

=cut
sub has_fields {
    my ($self, $fields) = @_;
    my @moves = @{$self->moves};
    #return 0 unless ($_ ~~ @moves) for (@{$fields});
    for (@{$fields}) {
        return 0 unless $_ ~~ @moves;
    }
    return 1
}

=head1 AUTHOR

Michael Stapelberg, E<lt>michael@stapelberg.deE<gt>

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2010-2011 Michael Stapelberg

This library is free software; it is licensed under the BSD license,
see the file LICENSE.

=cut

__PACKAGE__->meta->make_immutable;

1
