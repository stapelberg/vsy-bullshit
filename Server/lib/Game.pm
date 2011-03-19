# vim:ts=4:sw=4:expandtab
package Game;

use strict;
use Moose;
use Digest::SHA1 qw(sha1_hex);
use Player;

has 'size' => (is => 'ro', isa => 'Int', required => 1);
has 'id' => (is => 'ro', isa => 'Str', builder => 'build_id');
has 'words' => (
    traits => [ 'Array' ],
    is => 'ro',
    isa => 'ArrayRef[Str]',
    builder => 'build_words',
    handles => {
    }
);

has '_participants' => (
    traits => [ 'Array' ],
    is => 'rw',
    isa => 'ArrayRef[Player]',
    default => sub { [] },
    handles => {
        _add_player => 'push',
        participants => 'elements',
    }
);

# reads in a whole file
sub slurp {
    open my $fh, '<', shift;
    local $/;
    <$fh>;
}


sub build_id {
    my @random_values;
    push @random_values, rand(255) for (1..10);
    return sha1_hex(join('', @random_values));
}

sub build_words {
    my ($self) = @_;
    my $wordfile = slurp 'wordlists/default.txt';
    my @words = split("\n", $wordfile);
    # TODO: random $self->size * $self->size auswählen
    return [ @words ];
}

sub participates {
    my ($self, $player) = @_;

    my @matches = grep { $_ eq $player } $self->participants;
    return (@matches > 0);
}

sub add_player {
    my ($self, $player) = @_;

    return undef if $self->participates($player);

    $self->_add_player($player);

    return 1;
}

sub del_player {
    my ($self, $player) = @_;

    $self->_participants([ grep { $_ ne $player } $self->participants ]);
}

__PACKAGE__->meta->make_immutable;

1
