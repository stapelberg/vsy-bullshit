#!perl
# vim:ts=4:sw=4:expandtab

use strict;
use warnings;
use Test::More;
use Test::Exception;
use Data::Dumper;

use JSON::XS;
use LWP::UserAgent;

my $ua = LWP::UserAgent->new;

sub get {
    my ($path) = @_;

    my $response = $ua->get("http://localhost:8000/$path");

    ok($response->is_success, 'HTTP Request successful');
    my $content = $response->decoded_content;
    diag("Content = $content");
    my $json;
    lives_ok { $json = decode_json($content) } 'content can be decoded as JSON';

    return $json;
}

sub post {
    my ($path, $req) = @_;

    my $response = $ua->post(
        "http://localhost:8000/$path",
        Content_Type => 'application/json',
        Content => encode_json($req),
    );
    ok($response->is_success, 'HTTP Request successful');
    my $content = $response->decoded_content;
    diag("Content = $content");
    my $json;
    lives_ok { $json = decode_json($content) } 'content can be decoded as JSON';

    return $json;
}

my $nickname = 'sECuRE';

my $response = $ua->post(
    'http://localhost:8000/RegisterPlayer',
    Content_Type => 'application/json',
    Content => encode_json({ nickname => 'sECuRE' }),
);
ok($response->is_success, 'HTTP Request successful');
my $content = $response->decoded_content;
diag("Content = $content");
my $json;
lives_ok { $json = decode_json($content) } 'content can be decoded as JSON';

is($json->{success}, JSON::XS::true, 'Player successfully registered');
is($json->{error}, '', 'No error message set');
isnt($json->{token}, '', 'Token not empty');

my $token = $json->{token};

# try to register that nickname again
$response = $ua->post(
    'http://localhost:8000/RegisterPlayer',
    Content_Type => 'application/json',
    Content => encode_json({ nickname => 'sECuRE' }),
);
ok($response->is_success, 'HTTP Request successful');
$content = $response->decoded_content;
diag("Content = $content");
$json;
lives_ok { $json = decode_json($content) } 'content can be decoded as JSON';

is($json->{success}, JSON::XS::false, 'Player registration unsuccessful');
like($json->{error}, qr/exist/, 'Player already exists');

# check that no games exist so far
$json = get('CurrentGames');
is(@{$json}, 0, 'no games exist yet');

# create a game
$json = post('CreateGame', { token => $token });
is($json->{success}, JSON::XS::false, 'Game without size not created');

$json = post('CreateGame', { token => $token, size => 3 });
is($json->{success}, JSON::XS::true, 'Game created');
ok(exists $json->{id}, 'game id included in reply');
ok(exists $json->{words}, 'game words included in reply');
ok(exists $json->{name}, 'game name included in reply');
like($json->{name}, qr/^Unbenannt /, 'game name starts with "Unbenannt"');
is(@{$json->{words}}, 9, 'nine words for a 3x3 game delivered');
my $id = $json->{id};

$json = post('CreateGame', { token => $token, size => 3, name => 'fnord' });
is($json->{success}, JSON::XS::true, 'Game created');
ok(exists $json->{name}, 'game name included in reply');
is($json->{name}, 'fnord', 'game name is "fnord"');


# check that we can see the game
$json = get('CurrentGames');
is(@{$json}, 2, 'two games exists');
is_deeply($json->[0]->{participants}, [ $nickname ] , 'player nickname matches');

# leave the game
$json = post('LeaveGame', { token => $token, id => $id });
is($json->{success}, JSON::XS::true, 'Game left');

# check that it still exists, but participants is empty
$json = get('CurrentGames');
is(@{$json}, 2, 'still two games existing');
is_deeply($json->[0]->{participants}, [ ] , 'no participants');

# re-join the game
$json = post('JoinGame', { token => $token, id => $id });
is($json->{success}, JSON::XS::true, 'Game joined');
cmp_ok(@{$json->{words}}, '>', 0, '> 0 words in reply');

# check that we can see the game and we are in it again
$json = get('CurrentGames');
is(@{$json}, 2, 'two games exists');
is_deeply($json->[0]->{participants}, [ $nickname ] , 'player nickname matches');

# join again, should fail because we are already participating
$json = post('JoinGame', { token => $token, id => $id });
is($json->{success}, JSON::XS::true, 'Joining game again succeeds');

# check that we can see the game and we are in it again
$json = get('CurrentGames');
is(@{$json}, 2, 'two games exists');
is_deeply($json->[0]->{participants}, [ $nickname ] , 'player nickname matches');

# check for winner
$json = post('CheckWinner', { token => $token, id => $id });
is($json->{success}, JSON::XS::true, 'CheckWinner successful');
is($json->{winner}, undef, 'No winner yet');

# check the diagonal line
$json = post('MakeMove', { token => $token, id => $id, field => 0 });
is($json->{success}, JSON::XS::true, 'MakeMove successful');
$json = post('MakeMove', { token => $token, id => $id, field => 4 });
is($json->{success}, JSON::XS::true, 'MakeMove successful');
$json = post('MakeMove', { token => $token, id => $id, field => 8 });
is($json->{success}, JSON::XS::true, 'MakeMove successful');

# check that we have won
$json = post('CheckWinner', { token => $token, id => $id });
is($json->{success}, JSON::XS::true, 'CheckWinner successful');
is($json->{winner}, $nickname, 'I am the winner');

$json = post('CreateGame', { token => $token, size => 3, wordlist => 'wahl' });
is($json->{success}, JSON::XS::true, 'Game created');
ok(exists $json->{id}, 'game id included in reply');
ok(exists $json->{words}, 'game words included in reply');
diag('words = ' . Dumper($json->{words}));

done_testing;
