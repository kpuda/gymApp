import React, { useState } from 'react';
import { withFirebase } from '../Firebase';
import { Link } from 'react-router-dom';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

import Snackbar from '@material-ui/core/Snackbar';

function PasswordForget(props) {

  const [open, setOpen] = React.useState(false);
  const [openAlert, setOpenAlert] = React.useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const [state, setState] = useState({email: '', error: null})

  const handleChange = e => {
    const {name, value} = e.target;
    setState({...state, [name]: value});
  }

  const handleSubmit = () => {
    props.firebase
      .doPasswordReset(state.email)
      .then(() => {
        setState({email: '', error: null });
        handleClose();
        setOpenAlert(true);
      })
      .catch(error => {
        setState({ error });
      });
  };

  const isInvalid = state.email === '';

  return (
      <div>
        <Link to="" onClick={handleClickOpen}>
          Zapomniałeś hasła?
        </Link>
        <Dialog open={open} onClose={handleClose} aria-labelledby="form-dialog-title">
          <DialogTitle id="form-dialog-title">Zresetuj moje hasło</DialogTitle>
          <DialogContent>
            <DialogContentText>

Aby zresetować hasło, wprowadź swój adres e-mail. Niedługo potym otrzymasz instrukcje, jak zresetować hasło.
            </DialogContentText>
            <TextField
              autoFocus
              margin="dense"
              id="name"
              label="Adres email"
              type="email"
              name="email"
              value={state.email}
              onChange={handleChange}
              fullWidth
            />
              {state.error && <p style={{color:'red'}}>{state.error.message}</p>}

          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose} color="primary">
              Anuluj
            </Button>
            <Button onClick={handleSubmit} disabled={isInvalid} type="submit" color="primary">
              Resetuj hasło
            </Button>
          </DialogActions>
        </Dialog>

        <Snackbar
          anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'right',
          }}
          open={openAlert}
          autoHideDuration={6000}
          message="Link do resetowania hasła został pomyślnie wysłany"
        />

      </div>
    )
}

export default withFirebase(PasswordForget);
