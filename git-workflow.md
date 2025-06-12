# Git Workflow - PDS 2025

## 🌊 Flujo de Trabajo

### Setup Inicial
```bash
git clone git@github.com:RamiroPeidro/pds-2025.git
cd pds-2025
git checkout develop  # Rama principal de desarrollo
```

### Desarrollar Nueva Feature
```bash
git checkout develop
git pull origin develop
git checkout -b feature/nombre-descriptivo

# Trabajar en el código...
git add .
git commit -m "feat: descripción clara del cambio"
git push origin feature/nombre-descriptivo

# Crear Pull Request: feature/xxx → develop
```

### Hotfix Urgente
```bash
git checkout main
git checkout -b hotfix/descripcion-corta

# Arreglar el problema...
git add .
git commit -m "fix: descripción del hotfix"
git push origin hotfix/descripcion-corta

# Pull Request: hotfix/xxx → main
# Luego merge a develop también
```

### Release/Entrega
```bash
git checkout main
git merge develop
git tag v1.0.0
git push origin main --tags
```

## 📋 Convenciones de Commits

- `feat:` Nueva funcionalidad
- `fix:` Corrección de bug
- `docs:` Documentación
- `style:` Formato, punto y coma, etc
- `refactor:` Refactoring de código
- `test:` Agregando tests
- `chore:` Tareas de mantenimiento

## 🤝 Para Colaboradores

1. **Fork** el repositorio
2. **Clonar** tu fork
3. **Agregar upstream**: `git remote add upstream git@github.com:RamiroPeidro/pds-2025.git`
4. **Sincronizar**: `git fetch upstream && git merge upstream/develop`
5. **Desarrollar** en branch feature
6. **Pull Request** desde tu fork

## 🔄 Sincronización

```bash
# Actualizar develop
git checkout develop
git fetch origin
git merge origin/develop

# Actualizar main
git checkout main
git fetch origin
git merge origin/main
``` 