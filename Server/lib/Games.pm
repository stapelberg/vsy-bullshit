# vim:ts=4:sw=4:expandtab
package Games;

use strict;
use Moose;
use MooseX::Singleton;

has '_games' => (
    traits => ['Array'],
    is => 'rw',
    isa => 'ArrayRef',
    default => sub { [] },
    handles => {
        games => 'elements',
        add_game => 'push',
        filter => 'grep',
    }
);

sub by_id {
    my ($self, $id) = @_;

    my @matches = grep { $_->id eq $id } $self->games;
    return (@matches > 0 ? $matches[0] : undef);
}

sub del_game {
    my ($self, $game) = @_;

    my $id = $game->id;

    $self->_games([ $self->filter(sub { $_->id ne $id }) ]);
}

__PACKAGE__->meta->make_immutable;

1
