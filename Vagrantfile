Vagrant.configure("2") do |config|
  config.vm.box = "net9/ubuntu-24.04-arm64"

  # --- Configuration de la VM Base de Données (srv-db) ---
  config.vm.define "srv-db" do |db|
    db.vm.hostname = "srv-db"
    db.vm.network "private_network", ip: "192.168.56.11"
    db.vm.provision "shell", path: "scripts/install_mysql.sh"

    # Make available some files from scripts folder to the VM
    db.vm.synced_folder "./scripts", "/home/vagrant/scripts"
  end

  # --- Configuration de la VM Application (srv-back) ---
  config.vm.define "srv-back" do |back|
    back.vm.hostname = "srv-back"
    back.vm.network "private_network", ip: "192.168.56.10"
    back.vm.network "forwarded_port", guest: 8080, host: 8080
    back.vm.provision "shell", path: "scripts/install_java_maven.sh"
    back.vm.provision "shell", path: "scripts/install_tomcat.sh"
    back.vm.provision "shell", path: "scripts/deploy_back.sh"

    # Make available some files from scripts folder to the VM
    back.vm.synced_folder "./scripts", "/home/vagrant/scripts"
  end

  # --- Configuration de la VM Application (srv-front) ---
  config.vm.define "srv-front" do |front|
    front.vm.hostname = "srv-front"
    front.vm.network "private_network", ip: "192.168.56.12"
    front.vm.network "forwarded_port", guest: 80, host: 80
    front.vm.provision "shell", path: "scripts/install_node.sh"
    front.vm.provision "shell", path: "scripts/deploy_front.sh"

    # Make available some files from scripts folder to the VM
    front.vm.synced_folder "./scripts", "/home/vagrant/scripts"
  end

  config.vm.provider "virtualbox" do |vb|
    vb.memory = "2048"
    vb.cpus = 2
  end
end