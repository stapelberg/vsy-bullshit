# vim:ts=4:sw=4:expandtab
package Games;

use strict;
use Moose;
use MooseX::Singleton;

has '_games' => (
    traits => ['Array'],
    is => 'ro',
    isa => 'ArrayRef',
    default => sub { [] },
    handles => {
        games => 'elements',
        add_game => 'push',
    }
);

sub by_id {
    my ($self, $id) = @_;

    my @matches = grep { $_->id eq $id } $self->games;
    return (@matches > 0 ? $matches[0] : undef);
}

__PACKAGE__->meta->make_immutable;

1
