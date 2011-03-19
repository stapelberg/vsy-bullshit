use inc::Module::Install;

name 'Server';
all_from 'lib/BBServer.pm';

requires 'Tatsumaki';
requires 'JSON::XS';
requires 'Moose';
requires 'Try::Tiny';

test_requires 'Test::More';
test_requires 'Test::Exception';

WriteAll;
