# vim:ts=4:sw=4:expandtab
package Game;

use strict;
use Moose;
use Digest::SHA1 qw(sha1_hex);
use Player;
use PlayerState;
use Data::Dumper;
use v5.10;

=head1 NAME

Game - represents a running Bullshit Bingo Game

=head1 SYNOPSIS

 use Game;
 use Player;

 my $player = Player->new(nickname => 'sECuRE');
 my $game = Game->new(size => 3);

 $game->add_player($player);

 say "yes" if $game->participates($player); # prints 'yes'

 say $game->participants; # prints 'sECuRE'

 $game->make_move($player, 3);
 $game->make_move($player, 4);
 $game->make_move($player, 5);

 say $game->winner->nickname; # prints 'sECuRE'

=head1 ATTRIBUTES

=head2 size

Game size (one dimension). A size of 3 means the game spans a 3x3 field.

=cut
has 'size' => (is => 'ro', isa => 'Int', required => 1);
=head2 id

Unique identifier for this game.

=cut
has 'id' => (is => 'ro', isa => 'Str', builder => '_build_id');

=head2 words

The words which this game contains. For a size of 3, you will get an ArrayRef
of 3*3 strings.

=cut
has 'words' => (
    traits => [ 'Array' ],
    is => 'ro',
    isa => 'ArrayRef[Str]',
    builder => '_build_words',
    handles => {
    }
);

has '_participants' => (
    traits => [ 'Array' ],
    is => 'rw',
    isa => 'ArrayRef[PlayerState]',
    default => sub { [] },
    handles => {
        _add_player => 'push',
    }
);

has '_winning_fields' => (
    is => 'ro',
    isa => 'ArrayRef',
    builder => '_build_winning_fields',
);

=head1 METHODS
=cut

# reads in a whole file
sub _slurp {
    open my $fh, '<', shift;
    local $/;
    <$fh>;
}


sub _build_id {
    my @random_values;
    push @random_values, rand(255) for (1..10);
    return sha1_hex(join('', @random_values));
}

sub _build_words {
    my ($self) = @_;
    my $wordfile = _slurp 'wordlists/default.txt';
    my @words = split("\n", $wordfile);
    my @chosen;
    my $necessary = $self->size * $self->size;
    while (@chosen < $necessary) {
        my $idx = int(rand(@words - 1));
        push @chosen, $words[$idx];
        splice(@words, $idx, 1);
    }
    return [ @chosen ];
}

sub _build_winning_fields {
    my ($self) = @_;

    my @winning;
    my $size = $self->size;
    my $max = ($self->size - 1);

    for my $i (0..$max) {
        # check row
        push @winning, [ map { ($i * $size) + $_ } (0..$max) ];

        # check column
        push @winning, [ map { $i + ($_ * $size) } (0..$max) ];
    }

    # check diagonals
    push @winning, [ map { ($_ * $size) + $_ } (0..$max) ];
    push @winning, [ map { ($_ * $size) - $_ } (1..$size) ];

    return \@winning;
}

=head2 participants

Returns the nicknames of all participating players.

=cut
sub participants {
    my ($self) = @_;

    return map { $_->player->nickname } @{$self->_participants};
}

=head2 participates($player)

Checks if the given L<Player> is participating in this game.

=cut
sub participates {
    my ($self, $player) = @_;

    my @matches = grep { $_->player eq $player } @{$self->_participants};
    return (@matches > 0);
}

=head2 add_player($player)

Adds the given L<Player> to this game.

=cut
sub add_player {
    my ($self, $player) = @_;

    return undef if $self->participates($player);

    $self->_add_player(PlayerState->new(player => $player));

    return 1;
}

=head2 del_player($player)

Removes the given L<Player> from this game.

=cut
sub del_player {
    my ($self, $player) = @_;

    $self->_participants([ grep { $_->player ne $player } @{$self->_participants} ]);
}

=head2 make_move($player, $field)

Saves that the given L<Player> crossed the C<$field>.

=cut
sub make_move {
    my ($self, $player, $field) = @_;

    my ($state) = grep { $_->player eq $player } @{$self->_participants};

    return undef unless defined($state);

    $state->save_move($field);

    return 1
}

=head2 winner

Returns undef if nobody has won yet or the L<Player> who won the game.

=cut
sub winner {
    my ($self) = @_;

    for my $fields (@{$self->_winning_fields}) {
        for my $state (@{$self->_participants}) {
            if ($state->has_fields($fields)) {
                return $state->player;
            }
        }
    }

    return undef;
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
