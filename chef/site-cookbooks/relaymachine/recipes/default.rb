#
# Cookbook Name:: relaymachine
# Recipe:: default
#
# Copyright 2016, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#

directory '/var/log/upstart' do
  not_if '[[ -d /var/log/upstart ]]'
end

service 'relayrace' do
  provider Chef::Provider::Service::Upstart
  supports :start => true, :restart => true, :stop => true, :reload => true
  action :nothing 
end


## Main Platform
template '/etc/init/relayrace.conf' do
  source 'upstart.conf.erb'
  variables({
    :mac_user => ENV['SSH_USER'],
    :script_options => '-host aMaster'
  })
  notifies :enable, 'service[relayrace]'
  notifies :start, 'service[relayrace]'
  guard_interpreter :bash
  only_if '[[ $SSH_USER == "aMaster" ]]'
end

MACS = Integer(ENV['MACS'])
MAC_NUM = Integer(ENV['MAC_NUM'])
NTEAMS = Integer(ENV['NTEAMS'])

teams = (1..NTEAMS).map {|n| ('a'..'z').to_a[n - 1]}

slots = []
(0..MACS).each do |i|
  slots[i] = []
end

(1..NTEAMS).each do |n|
  (1..MACS).each do |j|
    if ((n - j) % MACS) == 0
      slots[j - 1].push(teams[n - 1] + '0:com.davidmiguel.relayrace.agents.RunnerAgent(false,' + teams[n - 1] + '1)')
      slots[j - 1].push(teams[n - 1] + '1:com.davidmiguel.relayrace.agents.RunnerAgent(true,' + teams[n - 1] + '2)')
    elsif (n % MACS) < j
      current = (1 + (j - (n % MACS)))
      if (current == MACS)
        pass = 0
      else
        pass = current + 1
      end
      slots[j - 1].push(teams[n - 1] + current.to_s + ':com.davidmiguel.relayrace.agents.RunnerAgent(false,' + teams[n - 1] + pass.to_s + ')')
    else
      current = (1 + ((MACS - (n % MACS)) + j))
      if (current == MACS)
        pass = 0
      else
        pass = current + 1
      end
      slots[j - 1].push(teams[n - 1] + current.to_s + ':com.davidmiguel.relayrace.agents.RunnerAgent(false,' + teams[n - 1] + pass.to_s + ')')
    end
  end
end

## Runners
template '/etc/init/relayrace.conf' do
  source 'upstart.conf.erb'
  variables({
    :mac_user => ENV['SSH_USER'],
    :script_options => '-container -host aMaster -agents "' + slots[MAC_NUM - 1].join(';') + '"'})
  notifies :enable, 'service[relayrace]'
  notifies :start, 'service[relayrace]'
  guard_interpreter :bash
  only_if '[[ $(echo $SSH_USER | grep Runner) ]]'
end


## Judge
template '/etc/init/relayrace.conf' do
  source 'upstart.conf.erb'
  variables({
    :mac_user => ENV['SSH_USER'],
    :script_options => '-container -host aMaster -agents "' +
      'JudgeAgent:com.davidmiguel.relayrace.agents.JudgeAgent(' +
      ENV['RELAY_ATTEMPTS'] +
      ',' + ENV['RELAY_LAPS'] +
      ',' + ENV['RELAY_STEP'] +')"'
  })
  notifies :enable, 'service[relayrace]'
  notifies :start, 'service[relayrace]'
  guard_interpreter :bash
  only_if '[[ $(echo $SSH_USER | grep Judge) ]]'
end


service 'relayrace_restart' do
  service_name 'relayrace'
  action :restart
  guard_interpreter :bash
  only_if '[[ $RELAY_SENTINEL == 1 ]]'
end

magic_shell_environment 'RELAY_SENTINEL' do
  value '1'
  guard_interpreter :bash
  only_if '[[ ! $RELAY_SENTINEL == 1 ]]'
end